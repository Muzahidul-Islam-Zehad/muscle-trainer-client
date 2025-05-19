package com.example.muscletrainer.dataProvider

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.muscletrainer.model.Workout
import com.example.muscletrainer.network.ApiService
import kotlinx.coroutines.launch

class WorkoutViewModel(private val apiService: ApiService) : ViewModel() {

    private val _workouts = MutableLiveData<List<Workout>>()
    val workouts: LiveData<List<Workout>> get() = _workouts

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchWorkoutsByType(type: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getWorkoutsByType(type)
                _workouts.postValue(response)
            } catch (e: Exception) {
                Log.e("WorkoutFetch", "Error: ${e.localizedMessage}", e)
                _error.postValue("Failed to fetch workouts: ${e.localizedMessage}")
            }
        }
    }
}
