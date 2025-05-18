package com.example.muscletrainer.utility

import java.text.SimpleDateFormat
import java.util.*

class CalculateAge {
    fun calculateAgeFromDateString(dateString: String): Int {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val birthDate: Date = sdf.parse(dateString) ?: return 0

        val today = Calendar.getInstance()
        val dob = Calendar.getInstance()
        dob.time = birthDate

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }
}
