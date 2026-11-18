package com.example.data.remote

import android.content.Context
import com.example.R
import com.example.data.local.AnnouncementEntity
import com.example.data.local.GameSessionEntity
import com.example.data.local.LeaderboardCacheEntity
import com.example.data.local.PlayerProfileEntity
import com.example.data.model.SyncStatus
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

/**
 * Contract defining the shared Firebase project synchronization boundary between
 * the Android offline-first client and the Phase 1 Online Web Administration Panel +
 * Cloud Functions backend verification pipeline.
 */
interface BackendSyncContract {
    suspend fun syncUserProfile(localProfile: PlayerProfileEntity): Result<RemoteUserProfile?>
    suspend fun uploadPendingSessions(pending: List<GameSessionEntity>): Result<Map<String, SyncStatus>>
    suspend fun fetchVerifiedLeaderboard(): Result<List<LeaderboardCacheEntity>>
    suspend fun fetchPublishedAnnouncements(): Result<List<AnnouncementEntity>>
}

data class EconomyConfig(
    val baseCorrectScore: Int = 100,
    val speedBonusMax: Int = 30,
    val noMistakeBonus: Int = 20,
    val specialMissionBonus: Int = 50,
    val stageXpReward: Int = 100,
    val stageCoinReward: Int = 25
)

class FirestoreSyncRepository(
    private val db: FirebaseFirestore?,
    private val auth: FirebaseAuth? = null
) : BackendSyncContract {

    constructor(context: Context) : this(
        db = if (FirebaseApp.getApps(context.applicationContext).isNotEmpty()) {
            try {
                FirebaseFirestore.getInstance(
                    context.applicationContext.getString(R.string.firestore_database_id)
                )
            } catch (_: Exception) {
                null
            }
        } else {
            null
        },
        auth = AuthManager.authOrNull(context)
    )

    private fun currentUidOrNull(): String? = auth?.currentUser?.uid?.takeIf { it.isNotBlank() }

    override suspend fun syncUserProfile(localProfile: PlayerProfileEntity): Result<RemoteUserProfile?> {
        val firestore = db
            ?: return Result.failure(IllegalStateException("اتصال ابری در این نسخه فعال نیست."))
        val uid = currentUidOrNull()
            ?: return Result.failure(IllegalStateException("ابتدا با حساب گوگل وارد شوید."))
        val path = "users/$uid"
        return try {
            val docRef = firestore.collection("users").document(uid)
            val snap = docRef.get().await()
            if (!snap.exists()) {
                val safeNick = localProfile.nickname.trim().ifEmpty { "قهرمان تأمین" }.take(30)
                val payload = mapOf(
                    "userId" to uid,
                    "nickname" to safeNick,
                    "selectedCharacterId" to localProfile.selectedCharacterId,
                    "departmentType" to localProfile.departmentType.take(64),
                    "role" to "player",
                    "authoritativeXp" to 0L,
                    "authoritativeCoins" to 0L,
                    "authoritativeMedals" to 0L,
                    "authoritativeScore" to 0L,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
                Result.success(null)
            } else {
                val remote = snap.toObject(
                    RemoteUserProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
                val safeNick = localProfile.nickname.trim().ifEmpty { "قهرمان تأمین" }.take(30)
                docRef.update(
                    mapOf(
                        "nickname" to safeNick,
                        "selectedCharacterId" to localProfile.selectedCharacterId,
                        "departmentType" to localProfile.departmentType.take(64),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
                Result.success(remote)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    override suspend fun uploadPendingSessions(
        pending: List<GameSessionEntity>
    ): Result<Map<String, SyncStatus>> {
        val firestore = db
            ?: return Result.failure(IllegalStateException("اتصال ابری در این نسخه فعال نیست."))
        val uid = currentUidOrNull()
            ?: return Result.failure(IllegalStateException("برای همگام‌سازی ابری وارد حساب گوگل شوید."))
        val results = mutableMapOf<String, SyncStatus>()
        for (session in pending) {
            val docId = session.operationId
            val path = "gameSessions/$docId"
            try {
                val docRef = firestore.collection("gameSessions").document(docId)
                val existing = docRef.get().await()
                if (!existing.exists()) {
                    val payload = mapOf(
                        "userId" to uid,
                        "operationId" to session.operationId,
                        "stageId" to session.stageId.toLong(),
                        "miniGameType" to session.miniGameType,
                        "rawScore" to session.rawScore.toLong().coerceIn(0L, 2000L),
                        "xpClaimed" to session.xpAwarded.toLong().coerceIn(0L, 500L),
                        "coinsClaimed" to session.coinsAwarded.toLong().coerceIn(0L, 250L),
                        "rating" to if (session.rating in listOf("BRONZE", "SILVER", "GOLD")) session.rating else "BRONZE",
                        "durationSeconds" to session.durationSeconds.toLong().coerceAtLeast(0L),
                        "correctCount" to session.correctCount.toLong().coerceAtLeast(0L),
                        "totalCount" to session.totalCount.toLong().coerceAtLeast(0L),
                        "verificationStatus" to "PENDING",
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    docRef.set(payload).await()
                    results[docId] = SyncStatus.SYNCED_PENDING_REVIEW
                } else {
                    val status = existing.getString("verificationStatus") ?: "PENDING"
                    results[docId] = when (status) {
                        "VERIFIED" -> SyncStatus.VERIFIED
                        "REJECTED" -> SyncStatus.REJECTED
                        else -> SyncStatus.SYNCED_PENDING_REVIEW
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.WRITE, path)
                results[docId] = SyncStatus.SYNC_FAILED
            }
        }
        return Result.success(results)
    }

    suspend fun refreshSubmittedSessionStatuses(uid: String): Result<Map<String, SyncStatus>> {
        val firestore = db ?: return Result.failure(IllegalStateException("اتصال ابری فعال نیست."))
        if (currentUidOrNull() != uid) {
            return Result.failure(IllegalStateException("شناسه کاربر نامعتبر است."))
        }
        val path = "gameSessions"
        return try {
            val snap = firestore.collection(path)
                .whereEqualTo("userId", uid)
                .get()
                .await()
            val statusMap = mutableMapOf<String, SyncStatus>()
            for (doc in snap.documents) {
                val opId = doc.getString("operationId") ?: doc.id
                val status = when (doc.getString("verificationStatus")) {
                    "VERIFIED" -> SyncStatus.VERIFIED
                    "REJECTED" -> SyncStatus.REJECTED
                    else -> SyncStatus.SYNCED_PENDING_REVIEW
                }
                statusMap[opId] = status
            }
            Result.success(statusMap)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            Result.failure(e)
        }
    }

    override suspend fun fetchVerifiedLeaderboard(): Result<List<LeaderboardCacheEntity>> {
        val firestore = db ?: return Result.failure(IllegalStateException("اتصال ابری فعال نیست."))
        val uid = currentUidOrNull()
            ?: return Result.failure(IllegalStateException("کاربر وارد نشده است."))
        val path = "leaderboards"
        return try {
            val snap = firestore.collection(path)
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .await()
            val entries = snap.documents.mapIndexedNotNull { index, doc ->
                val dto = doc.toObject(
                    RemoteLeaderboardEntry::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ) ?: return@mapIndexedNotNull null
                LeaderboardCacheEntity(
                    id = "online_${doc.id}",
                    rank = index + 1,
                    displayName = dto.nickname.ifBlank { "قهرمان" },
                    characterId = dto.selectedCharacterId.ifBlank { "hesabgar" },
                    level = dto.level.toInt().coerceIn(1, 8),
                    score = dto.score.toInt().coerceAtLeast(0),
                    isCurrentUser = dto.userId == uid,
                    isVerifiedOnline = true
                )
            }
            Result.success(entries)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            Result.failure(e)
        }
    }

    override suspend fun fetchPublishedAnnouncements(): Result<List<AnnouncementEntity>> {
        val firestore = db ?: return Result.failure(IllegalStateException("اتصال ابری فعال نیست."))
        if (currentUidOrNull() == null) {
            return Result.failure(IllegalStateException("کاربر وارد نشده است."))
        }
        val path = "announcements"
        return try {
            val snap = firestore.collection(path).get().await()
            val list = snap.documents.mapNotNull { doc ->
                val dto = doc.toObject(
                    RemoteAnnouncement::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ) ?: return@mapNotNull null
                if (!dto.active || dto.title.isBlank()) return@mapNotNull null
                AnnouncementEntity(
                    id = "remote_${doc.id}",
                    title = dto.title,
                    body = dto.body,
                    badgeText = dto.badgeText.ifBlank { "اطلاعیه ابری" },
                    jalaliDate = dto.jalaliDate,
                    isSpecialEvent = false,
                    daysRemaining = 0
                )
            }
            Result.success(list)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            Result.failure(e)
        }
    }

    suspend fun fetchEconomyConfig(): Result<EconomyConfig> {
        val firestore = db ?: return Result.failure(IllegalStateException("اتصال ابری فعال نیست."))
        if (currentUidOrNull() == null) {
            return Result.failure(IllegalStateException("کاربر وارد نشده است."))
        }
        val path = "settings/economy"
        return try {
            val doc = firestore.collection("settings").document("economy").get().await()
            if (!doc.exists()) return Result.success(EconomyConfig())
            val dto = doc.toObject(
                RemoteGameSettings::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            ) ?: return Result.success(EconomyConfig())
            Result.success(
                EconomyConfig(
                    baseCorrectScore = dto.baseCorrectScore.toInt().coerceIn(10, 500),
                    stageXpReward = dto.stageXpReward.toInt().coerceIn(10, 500),
                    stageCoinReward = dto.stageCoinReward.toInt().coerceIn(5, 200)
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }
}
