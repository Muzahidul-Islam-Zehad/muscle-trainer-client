package com.example.muscletrainer

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var listener: FirebaseAuth.AuthStateListener? = null

    fun init(onUserChanged: (FirebaseUser?) -> Unit) {
        listener = FirebaseAuth.AuthStateListener {
            onUserChanged(it.currentUser)
        }
        auth.addAuthStateListener(listener!!)
    }

    fun cleanup() {
        listener?.let {
            auth.removeAuthStateListener(it)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun signOut(activity: Activity) {
        auth.signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(activity, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            googleSignInClient.revokeAccess()
        }
    }
}
