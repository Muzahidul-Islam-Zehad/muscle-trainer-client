package com.example.muscletrainer.dataProvider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muscletrainer.model.Meal
import com.example.muscletrainer.network.ApiService
import kotlinx.coroutines.launch

class MealViewModel(private val apiService: ApiService) : ViewModel() {

    private val _meals = MutableLiveData<List<Meal>>()
    val meals: LiveData<List<Meal>> = _meals

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


    fun fetchMealsByType(type: String) {
        viewModelScope.launch {
            try {

                val response = apiService.getMealsByType(type)
                _meals.value = response
            } catch (e: Exception) {
                _error.value = "Failed to load meals: ${e.message}"
            }
        }
    }
}
