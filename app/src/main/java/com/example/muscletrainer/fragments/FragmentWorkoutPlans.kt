package com.example.muscletrainer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.muscletrainer.R
import com.example.muscletrainer.adapter.WorkoutAdapter
import com.example.muscletrainer.dataProvider.WorkoutViewModel
import com.example.muscletrainer.factory.WorkoutViewModelFactory
import com.example.muscletrainer.network.RetrofitInstance

class FragmentWorkoutPlans : Fragment() {

    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var loadingOverlay: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_plans, container, false)

        val spinner: Spinner = view.findViewById(R.id.spinnerWorkoutType)
        recyclerView = view.findViewById(R.id.recyclerWorkouts)
        recyclerView.layoutManager = LinearLayoutManager(context)

        loadingOverlay = view.findViewById(R.id.loading_overlay)

        workoutAdapter = WorkoutAdapter(mutableListOf())
        recyclerView.adapter = workoutAdapter

        val apiService = RetrofitInstance.api
        workoutViewModel = ViewModelProvider(
            this,
            WorkoutViewModelFactory(apiService)
        )[WorkoutViewModel::class.java]

        workoutViewModel.workouts.observe(viewLifecycleOwner) {
            loadingOverlay.animate().alpha(0f).setDuration(200).withEndAction {
                loadingOverlay.visibility = View.GONE
            }.start()

            workoutAdapter.updateData(it)
        }

        // Add spinner options
        val workoutTypes = listOf("cardio", "legs", "stretching", "flexibility", "full_body", "core","upper_body")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, workoutTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        workoutViewModel.error.observe(viewLifecycleOwner) {
            loadingOverlay.animate().alpha(0f).setDuration(200).withEndAction {
                loadingOverlay.visibility = View.GONE
            }.start()

            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        // Set listener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedType = workoutTypes[position]
                loadingOverlay.alpha = 0f
                loadingOverlay.visibility = View.VISIBLE
                loadingOverlay.animate().alpha(1f).setDuration(200).start()
                workoutViewModel.fetchWorkoutsByType(selectedType)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        return view
    }
}
