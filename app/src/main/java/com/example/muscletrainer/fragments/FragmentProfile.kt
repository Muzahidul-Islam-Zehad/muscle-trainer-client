package com.example.muscletrainer.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.muscletrainer.AuthManager
import com.example.muscletrainer.LoginPage
import com.example.muscletrainer.R
import com.example.muscletrainer.network.RetrofitInstance
import com.example.muscletrainer.utility.CalculateAge
import kotlinx.coroutines.launch

class FragmentProfile : Fragment() {

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
        val ageText = view.findViewById<TextView>(R.id.agetext)
        val heightText = view.findViewById<TextView>(R.id.heighttext)
        val weightText = view.findViewById<TextView>(R.id.weigthtext)
        val bmiText = view.findViewById<TextView>(R.id.bmitext)
        val userEmail = view.findViewById<TextView>(R.id.userEmail)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)

        val user = AuthManager.getCurrentUser()
        val userName = user?.displayName ?: "Example User"
        val email = user?.email ?: "example@mail.com"

        tvUserName.text = userName
        userEmail.text = email

        // 🚀 Fetch personal info from server

        lifecycleScope.launch {
            try {
                val personalInfo = RetrofitInstance.api.getPersonalInfo(email)
                Toast.makeText(requireContext(), "$personalInfo", Toast.LENGTH_LONG).show()

                genderText.text = "Gender : ${personalInfo.gender}"
                ageText.text = "Age : ${CalculateAge().calculateAgeFromDateString(personalInfo.birth_date).toString()}"
                heightText.text = "Height : ${personalInfo.height_cm} cm"
                weightText.text = "Weight : ${personalInfo.weight_kg} kg"
                bmiText.text = "BMI : ${personalInfo.bmi.toString()}"



            } catch (e: Exception) {
                Log.e("ProfileFragment", "Error fetching personal info", e)
            }
        }


        // 🔓 Logout
        logoutButton.setOnClickListener {
            AuthManager.signOut(requireActivity())
            val intent = Intent(requireContext(), LoginPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
