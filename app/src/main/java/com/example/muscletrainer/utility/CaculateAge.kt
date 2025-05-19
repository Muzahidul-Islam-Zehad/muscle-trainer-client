package com.example.muscletrainer.utility

import java.text.SimpleDateFormat
import java.util.*

class CalculateAge {
    fun calculateAgeFromDateString(dateString: String): Int {
        return try {
            // Normalize input: add leading zeros if needed
            val parts = dateString.split("/", "\\").map { it.trim() }
            if (parts.size != 3) return 0

            val day = parts[0].padStart(2, '0')
            val month = parts[1].padStart(2, '0')
            val year = parts[2]

            val normalizedDate = "$day/$month/$year"

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            val birthDate: Date = sdf.parse(normalizedDate) ?: return 0

            val today = Calendar.getInstance()
            val dob = Calendar.getInstance()
            dob.time = birthDate

            var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

            // If birthday hasn’t occurred this year yet, subtract one
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--
            }

            age
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
}
