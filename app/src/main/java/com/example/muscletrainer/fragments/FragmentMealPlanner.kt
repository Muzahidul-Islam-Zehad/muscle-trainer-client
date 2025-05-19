package com.example.muscletrainer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.muscletrainer.R
import com.example.muscletrainer.adapter.MealAdapter
import com.example.muscletrainer.dataProvider.MealViewModel
import com.example.muscletrainer.factory.MealViewModelFactory
import com.example.muscletrainer.network.RetrofitInstance

class FragmentMealPlanner : Fragment() {

    private lateinit var mealViewModel: MealViewModel
    private lateinit var spinnerMealType: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var mealAdapter: MealAdapter
    private lateinit var loadingOverlay: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_meal_planner, container, false)

        spinnerMealType = view.findViewById(R.id.spinnerMealType)
        recyclerView = view.findViewById(R.id.recyclerMeals)

        loadingOverlay = view.findViewById(R.id.loading_overlay)

        recyclerView.layoutManager = LinearLayoutManager(context)
        mealAdapter = MealAdapter(mutableListOf())
        recyclerView.adapter = mealAdapter

        val apiService = RetrofitInstance.api
        mealViewModel = ViewModelProvider(
            this, MealViewModelFactory(apiService)
        )[MealViewModel::class.java]

        mealViewModel.meals.observe(viewLifecycleOwner) {
            loadingOverlay.visibility = View.GONE
            mealAdapter.updateData(it)
        }

        mealViewModel.error.observe(viewLifecycleOwner) {
            loadingOverlay.visibility = View.GONE
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }


        val mealTypes = listOf("breakfast", "lunch", "dinner", "snack")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mealTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMealType.adapter = adapter

        spinnerMealType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedType = mealTypes[position]
                loadingOverlay.visibility = View.VISIBLE
                mealViewModel.fetchMealsByType(selectedType)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return view
    }
}
