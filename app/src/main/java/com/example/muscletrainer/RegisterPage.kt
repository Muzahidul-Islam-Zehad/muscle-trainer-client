package com.example.muscletrainer

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.muscletrainer.model.User
import com.example.muscletrainer.network.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterPage : AppCompatActivity() {
    private lateinit var googleAuthManager: GoogleAuthManager
    private lateinit var auth: FirebaseAuth

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        googleAuthManager.handleSignInResult(result.data)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()

        val nameEditText = findViewById<EditText>(R.id.userNameInput)
        val emailEditText = findViewById<EditText>(R.id.userEmailInput)
        val passwordEditText = findViewById<EditText>(R.id.userPassInput)
        val passwordToggle = findViewById<ImageView>(R.id.passwordToggle)
        val checkBox = findViewById<CheckBox>(R.id.checkBox)
        val registerButton = findViewById<Button>(R.id.button)
        val loginTextView = findViewById<TextView>(R.id.textView10)

        // Disable register button initially
        registerButton.isEnabled = false

        var isPasswordVisible = false

        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                // Show password
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_show) // Use closed-eye icon
            } else {
                // Hide password
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_not_show) // Use open-eye icon
            }
            // Move cursor to end after toggling
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        fun isFormValid(): Boolean {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            return name.isNotEmpty() && isEmailValid && password.isNotEmpty() && password.length >=6 && checkBox.isChecked
        }

        // Check all fields when anything changes
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                registerButton.isEnabled = isFormValid()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        nameEditText.addTextChangedListener(watcher)
        emailEditText.addTextChangedListener(watcher)
        passwordEditText.addTextChangedListener(watcher)

        checkBox.setOnCheckedChangeListener { _, _ ->
            registerButton.isEnabled = isFormValid()
        }

        val loadingOverlay = findViewById<FrameLayout>(R.id.loading_overlay)
        registerButton.setOnClickListener {

            val userName = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.length >= 6) {
                loadingOverlay.visibility = View.VISIBLE

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser

                            //  Update Firebase display name
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(userName)
                                .build()

                            user?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        //  Send to backend after profile updated
                                        val timezoneId = java.util.TimeZone.getDefault().id
                                        createUser(User(userName = userName, email = email, location = timezoneId))

                                        Toast.makeText(this, "Registered: ${user.email}", Toast.LENGTH_SHORT).show()

                                        loadingOverlay.visibility = View.GONE

                                        //  Navigate to next screen
                                        startActivity(Intent(this, CompleteProfile::class.java))
                                        finish()
                                    } else {
                                        loadingOverlay.visibility = View.GONE
                                        Toast.makeText(this, "Profile update failed: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }

                        } else {
                            loadingOverlay.visibility = View.GONE
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Enter valid email and password (min 6 characters)", Toast.LENGTH_SHORT).show()
            }
        }

//
        loginTextView.setOnClickListener {
            val intent = Intent(this@RegisterPage, LoginPage::class.java)
            startActivity(intent)
            finish()
        }


        findViewById<ImageButton>(R.id.google1).setOnClickListener {
            googleAuthManager.startSignIn()
        }
        //GOOGLE LOGIN DETAILS
        googleAuthManager = GoogleAuthManager(
            activity = this,
            launcher = launcher,
            onSuccess = { user ->
                val timezoneId = java.util.TimeZone.getDefault().id
                val email = user.email ?: ""

                loadingOverlay.visibility = View.VISIBLE

                // Create user in users table
                createUser(
                    User(
                        userName = user.displayName ?: "",
                        email = email,
                        location = timezoneId
                    )
                )

                // Now check if personal info already exists
                lifecycleScope.launch {
                    try {
                        val exists = RetrofitInstance.api.checkUserInfoExists(email)

                        if (exists) {
                            // If personal info exists, go to LandingPage
                            startActivity(Intent(this@RegisterPage, LandingPage::class.java))
                        } else {
                            // Else go to CompleteProfile
                            startActivity(Intent(this@RegisterPage, CompleteProfile::class.java))
                        }

                        loadingOverlay.visibility = View.GONE
                        finish() // Optional: to close LoginPage

                    } catch (e: Exception) {
                        loadingOverlay.visibility = View.GONE
                        Toast.makeText(
                            this@RegisterPage,
                            "Error checking user info: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            onError = { error ->
                Toast.makeText(this, "Login failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )




        //test logout button
//        findViewById<Button>(R.id.logout).setOnClickListener {
//            AuthManager.signOut(this)
//            Toast.makeText(this, "current user -> ${AuthManager.getCurrentUser()}", Toast.LENGTH_SHORT).show()
//        }

        //test button for fetch user
//        findViewById<Button>(R.id.testGetUser).setOnClickListener {
//            fetchUsers()
//        }

    }

//    private fun fetchUsers() {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val users = RetrofitInstance.api.getUsers()
//                withContext(Dispatchers.Main){
//                    Toast.makeText(this@RegisterPage, "$users", Toast.LENGTH_LONG).show()
//                }
//            }
//            catch (e : Exception)
//            {
//                e.printStackTrace()
//            }
//
//        }
//    }
    //create user fun
    private fun createUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.createUser(user)
                withContext(Dispatchers.Main) {
//                    println("Create user status: ${response.code()}")
                    Toast.makeText(this@RegisterPage, "inserted : $response", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}