package com.example.muscletrainer

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.muscletrainer.model.PersonalInfo
import com.example.muscletrainer.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class CompleteProfile : AppCompatActivity() {

    private var selectedGender: String = "Male" // default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_complete_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Spinner setup
        val genderList = listOf("Male", "Female")
        val spinner: Spinner = findViewById(R.id.gender)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedGender = genderList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Date picker
        val dateInput = findViewById<EditText>(R.id.birthDate)
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateInput.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        // Button click
        findViewById<Button>(R.id.go_landing).setOnClickListener {
            val birthDate = dateInput.text.toString()
            val weightText = findViewById<EditText>(R.id.weightedit).text.toString()
            val heightText = findViewById<EditText>(R.id.heightedit).text.toString()

            if (birthDate.isBlank() || weightText.isBlank() || heightText.isBlank()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val weight = weightText.toDoubleOrNull()
            val height = heightText.toDoubleOrNull()

            if (weight == null || height == null) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bmi = (weight * 10000) / (height * height)

            val personalInfo = PersonalInfo(
                email = AuthManager.getCurrentUser()?.email ?: "",
                gender = selectedGender,
                birth_date = birthDate,
                weight_kg = weight,
                height_cm = height,
                bmi = bmi
            )

            // 🔥 Send data to backend using Retrofit
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.api.setPersonalInfo(personalInfo)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@CompleteProfile, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@CompleteProfile, LandingPage::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@CompleteProfile, "Failed to save profile", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CompleteProfile, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
