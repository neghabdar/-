package com.example.data.remote

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Keep
@IgnoreExtraProperties
data class RemoteUserProfile(
    @DocumentId val docId: String = "",
    val userId: String = "",
    val nickname: String = "",
    val selectedCharacterId: String = "hesabgar",
    val departmentType: String = "",
    val role: String = "player",
    val authoritativeXp: Long = 0L,
    val authoritativeCoins: Long = 0L,
    val authoritativeMedals: Long = 0L,
    val authoritativeScore: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class RemoteGameSession(
    @DocumentId val docId: String = "",
    val userId: String = "",
    val operationId: String = "",
    val stageId: Long = 1L,
    val miniGameType: String = "VAULT_PUZZLE",
    val rawScore: Long = 0L,
    val xpClaimed: Long = 0L,
    val coinsClaimed: Long = 0L,
    val rating: String = "BRONZE",
    val durationSeconds: Long = 0L,
    val correctCount: Long = 0L,
    val totalCount: Long = 0L,
    val verificationStatus: String = "PENDING",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class RemoteLeaderboardEntry(
    @DocumentId val docId: String = "",
    val userId: String = "",
    val nickname: String = "",
    val selectedCharacterId: String = "hesabgar",
    val level: Long = 1L,
    val score: Long = 0L,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class RemoteAnnouncement(
    @DocumentId val docId: String = "",
    val title: String = "",
    val body: String = "",
    val badgeText: String = "",
    val jalaliDate: String = "",
    val active: Boolean = true,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class RemoteGameSettings(
    @DocumentId val docId: String = "",
    val baseCorrectScore: Long = 100L,
    val stageXpReward: Long = 100L,
    val stageCoinReward: Long = 25L,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
