package com.example.muscletrainer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.muscletrainer.R
import com.example.muscletrainer.model.Meal

class MealAdapter(private var meals: MutableList<Meal>) :
    RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    fun updateData(newMeals: List<Meal>) {
        meals.clear()
        meals.addAll(newMeals)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meal, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]
        holder.bind(meal)
    }

    override fun getItemCount(): Int = meals.size

    class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mealImage: ImageView = itemView.findViewById(R.id.imgMeal)
        private val mealName: TextView = itemView.findViewById(R.id.txtMealName)
        private val mealDetails: TextView = itemView.findViewById(R.id.txtMealDetails)
        private val mealDescription: TextView = itemView.findViewById(R.id.txtMealDescription)

        fun bind(meal: Meal) {
            mealName.text = meal.m_name
            mealDescription.text = meal.m_description
            mealDetails.text = "Calories: ${meal.m_calories} \n Protein: ${meal.m_protein}g \n Carbs: ${meal.m_carbs}g \n Fat: ${meal.m_fat}g"

            Glide.with(itemView.context)
                .load(meal.m_image)
                .placeholder(R.drawable.meal_placeholder) // Add a placeholder image in your drawable folder
                .into(mealImage)
        }
    }
}
