package com.example.muscletrainer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.muscletrainer.network.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


//it's just a splash screen ------ nothing else

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.boarding2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            delay(1500)
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                val email = AuthManager.getCurrentUser()?.email ?: return@launch
                val exists = RetrofitInstance.api.checkUserInfoExists(email)

                Toast.makeText(this@MainActivity, "$exists", Toast.LENGTH_SHORT).show()

                if (exists) {
                    val intent = Intent(this@MainActivity, LandingPage::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@MainActivity, CompleteProfile::class.java)
                    startActivity(intent)
                }
            } else {
                val intent = Intent(this@MainActivity, OnBoarding1::class.java)
                startActivity(intent)
            }

            finish()
        }

    }
}