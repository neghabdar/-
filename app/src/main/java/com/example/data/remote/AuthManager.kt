package com.example.data.remote

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object AuthManager {
    private const val TAG = "Auth"

    fun isFirebaseAvailable(context: Context): Boolean {
        return try {
            FirebaseApp.getApps(context.applicationContext).isNotEmpty()
        } catch (_: Exception) {
            false
        }
    }

    fun authOrNull(context: Context): FirebaseAuth? {
        return if (isFirebaseAvailable(context)) {
            try {
                Firebase.auth
            } catch (_: Exception) {
                null
            }
        } else {
            null
        }
    }

    fun authStateFlow(auth: FirebaseAuth?): Flow<FirebaseUser?> = callbackFlow {
        if (auth == null) {
            trySend(null)
            awaitClose {}
            return@callbackFlow
        }
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    @SuppressLint("DiscouragedApi")
    private fun getDefaultWebClientId(context: Context): String? {
        return try {
            val resId = context.resources.getIdentifier(
                "default_web_client_id",
                "string",
                context.packageName
            )
            if (resId != 0) context.getString(resId).takeIf { it.isNotBlank() } else null
        } catch (_: Exception) {
            null
        }
    }

    fun attemptAutoSignIn(
        context: Context,
        scope: CoroutineScope,
        onAuthSuccess: (FirebaseUser) -> Unit = {},
        onUnauthenticated: () -> Unit = {}
    ) {
        val auth = authOrNull(context) ?: run {
            onUnauthenticated()
            return
        }
        val existing = auth.currentUser
        if (existing != null) {
            onAuthSuccess(existing)
            return
        }
        val clientId = getDefaultWebClientId(context) ?: run {
            onUnauthenticated()
            return
        }
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val cred = credentialManager.getCredential(context, request).credential
                if (cred is CustomCredential && cred.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val token = GoogleIdTokenCredential.createFrom(cred.data).idToken
                    val result = Firebase.auth.signInWithCredential(
                        GoogleAuthProvider.getCredential(token, null)
                    ).await()
                    val user = result.user
                    if (user != null) onAuthSuccess(user) else onUnauthenticated()
                } else {
                    onUnauthenticated()
                }
            } catch (_: Exception) {
                onUnauthenticated()
            }
        }
    }

    fun onGoogleSignInClicked(
        context: Context,
        scope: CoroutineScope,
        onAuthSuccess: (FirebaseUser) -> Unit,
        onAuthError: (String) -> Unit,
        onAuthCancelled: () -> Unit = {}
    ) {
        val auth = authOrNull(context) ?: run {
            onAuthError("در این نسخه سرویس ورود ابری پیکربندی نشده است؛ بازی به‌صورت آفلاین ادامه دارد.")
            return
        }
        val clientId = getDefaultWebClientId(context) ?: run {
            onAuthError("پیکربندی ورود با گوگل یافت نشد.")
            return
        }
        val activity = context as? Activity
        if (activity == null) {
            onAuthError("امکان باز کردن پنجره ورود در این نما وجود ندارد.")
            return
        }

        val credentialManager = CredentialManager.create(context)
        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        scope.launch {
            try {
                val cred = credentialManager.getCredential(activity, request).credential
                if (cred is CustomCredential && cred.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val token = GoogleIdTokenCredential.createFrom(cred.data).idToken
                    val result = Firebase.auth.signInWithCredential(
                        GoogleAuthProvider.getCredential(token, null)
                    ).await()
                    val user = result.user
                    if (user != null) {
                        onAuthSuccess(user)
                    } else {
                        onAuthError("ورود تکمیل نشد؛ لطفاً دوباره تلاش کنید.")
                    }
                } else {
                    onAuthError("نوع اعتبارنامه دریافتی معتبر نیست.")
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w(TAG, "Google Sign-In cancelled or dismissed: ${e.message}", e)
                onAuthCancelled()
            } catch (e: Exception) {
                Log.e(TAG, "Google Sign-In failed", e)
                onAuthError("ورود به حساب ابری انجام نشد: ${e.localizedMessage ?: "خطای ناشناخته"}")
            }
        }
    }

    fun signOut(
        context: Context,
        scope: CoroutineScope,
        onSignOutComplete: () -> Unit = {}
    ) {
        val credentialManager = CredentialManager.create(context)
        scope.launch {
            withContext(NonCancellable) {
                try {
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to clear credential state: ${e.message}")
                } finally {
                    authOrNull(context)?.signOut()
                    onSignOutComplete()
                }
            }
        }
    }
}
