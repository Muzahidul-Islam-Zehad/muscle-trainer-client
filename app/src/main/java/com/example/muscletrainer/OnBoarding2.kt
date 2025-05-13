package com.example.muscletrainer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.muscletrainer.OnBoarding1

class OnBoarding2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_on_boarding2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.boarding2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nextBtn = findViewById<ImageButton>(R.id.nextOne)
        nextBtn?.setOnClickListener {
            startActivity(Intent(this@OnBoarding2, OnBoarding3::class.java))
        }
    }
}