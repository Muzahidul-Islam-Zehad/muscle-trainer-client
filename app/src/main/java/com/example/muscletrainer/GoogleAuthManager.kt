package com.example.muscletrainer

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthManager(
    private val activity: Activity,
    private val launcher: ActivityResultLauncher<Intent>,
    private val onSuccess: (FirebaseUser) -> Unit,
    private val onError: (Exception) -> Unit
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id)) // ✅ Ensure this exists in strings.xml
            .requestEmail()
            .build()
        GoogleSignIn.getClient(activity, gso)
    }

    fun startSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    fun handleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { result ->
                    if (result.isSuccessful) {
                        onSuccess(auth.currentUser!!)
                    } else {
                        onError(result.exception ?: Exception("Authentication failed"))
                    }
                }
        } catch (e: ApiException) {
            onError(e)
        }
    }
}
