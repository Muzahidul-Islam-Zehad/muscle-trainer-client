package com.example.muscletrainer.model

data class Meal(
    val m_id: String,            // UUID
    val m_name: String,
    val m_description: String,
    val m_type: String,          // e.g., "breakfast", "lunch", "dinner", "snack"
    val m_calories: Float,
    val m_protein: Float,
    val m_carbs: Float,
    val m_fat: Float,
    val m_image: String          // Image URL
)

