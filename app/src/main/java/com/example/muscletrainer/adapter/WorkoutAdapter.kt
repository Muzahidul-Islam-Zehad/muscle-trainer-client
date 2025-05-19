package com.example.muscletrainer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.muscletrainer.R
import com.example.muscletrainer.model.Workout

class WorkoutAdapter(private val workouts: MutableList<Workout>) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.workoutTitle)
        val description: TextView = view.findViewById(R.id.workoutDescription)
        val webView: WebView = view.findViewById(R.id.workoutVideo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.workout_item, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.title.text = workout.w_title
        holder.description.text = workout.w_description

        val embedUrl = workout.w_video.replace("watch?v=", "embed/")
        holder.webView.settings.javaScriptEnabled = true
        holder.webView.settings.loadWithOverviewMode = true
        holder.webView.settings.useWideViewPort = true
        holder.webView.settings.pluginState = WebSettings.PluginState.ON
        holder.webView.loadUrl(embedUrl)
    }

    override fun getItemCount(): Int = workouts.size

    fun updateData(newWorkouts: List<Workout>) {
        workouts.clear()
        workouts.addAll(newWorkouts)
        notifyDataSetChanged()
    }
}
