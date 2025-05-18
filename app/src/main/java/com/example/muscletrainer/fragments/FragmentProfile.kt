package com.example.muscletrainer.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.muscletrainer.AuthManager
import com.example.muscletrainer.LoginPage
import com.example.muscletrainer.R
import com.example.muscletrainer.dataProvider.SharedViewModel
import com.example.muscletrainer.utility.CalculateAge

class FragmentProfile : Fragment() {

    // Get ViewModel instance
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvUserName = view.findViewById<TextView>(R.id.userName)
        val genderText = view.findViewById<TextView>(R.id.genderText)
        val ageText = view.findViewById<TextView>(R.id.agetext)       // make sure ID matches your XML
        val heightText = view.findViewById<TextView>(R.id.heighttext) // make sure ID matches your XML
        val weightText = view.findViewById<TextView>(R.id.weigthtext) // make sure ID matches your XML
        val bmi = view.findViewById<TextView>(R.id.bmitext)

        val userEmail = view.findViewById<TextView>(R.id.userEmail)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)

        // Set userName and email from AuthManager (if you want)
        val userName = AuthManager.getCurrentUser()?.displayName ?: "Example User"
        tvUserName.text = userName
        val email = AuthManager.getCurrentUser()?.email ?: "Example@mail.com"
        userEmail.text = email

        // Observe User LiveData if you want to update user-related info dynamically
        sharedViewModel.user.observe(viewLifecycleOwner, Observer { user ->
            // For example, update username if changed via ViewModel
            tvUserName.text = user.userName ?: userName
            userEmail.text = user.email ?: email
        })

        // Observe PersonalInfo LiveData for gender, age, height, weight
        sharedViewModel.personalInfo.observe(viewLifecycleOwner, Observer { info ->
            genderText.text = info.gender ?: "N/A"
            ageText.text = CalculateAge().calculateAgeFromDateString(info.birthDate).toString()?.toString() ?: "N/A"
            heightText.text = info.height?.toString() ?: "N/A"
            weightText.text = info.weight?.toString() ?: "N/A"
            bmi.text = info.bmi?.toString() ?: "N/A"
        })

        // Logout button click
        logoutButton.setOnClickListener {
            AuthManager.signOut(requireActivity())
            val intent = Intent(requireContext(), LoginPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }

}
