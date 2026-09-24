package com.minhaaprovacao

import android.app.Application
import com.google.firebase.auth.FirebaseAuth

class MinhaAprovacaoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    // Fallback or silent handling
                }
            }
        }
    }
}
