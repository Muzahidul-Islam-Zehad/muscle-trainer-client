package com.example.muscletrainer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.muscletrainer.model.User
import com.example.muscletrainer.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginPage : AppCompatActivity() {
    private lateinit var googleAuthManager: GoogleAuthManager

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        googleAuthManager.handleSignInResult(result.data)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //GOOGLE LOGIN DETAILS
        googleAuthManager = GoogleAuthManager(
            activity = this,
            launcher = launcher,
            onSuccess = { user ->
                val timezoneId = java.util.TimeZone.getDefault().id
                createUser(
                    User(
                        userName = user.displayName?:"",
                        email = user.email?:"",
                        location = timezoneId ?: ""
                    )
                )
                Toast.makeText(this@LoginPage, "usr name: ${user.displayName}", Toast.LENGTH_SHORT).show()
                // Navigate to next screen
                startActivity(Intent(this, CompleteProfile::class.java))
            },
            onError = { error ->
                Toast.makeText(this, "Login failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )


        findViewById<ImageButton>(R.id.googleLogin).setOnClickListener {
            googleAuthManager.startSignIn()
        }




        val emailEditText = findViewById<EditText>(R.id.userEmailInput)
        val loginButton = findViewById<Button>(R.id.button2)
        val passwordEditText = findViewById<EditText>(R.id.userPassInput)
        val passwordToggle = findViewById<ImageView>(R.id.passwordToggle) //
        val loginTextView = findViewById<TextView>(R.id.textView11)

        // disable the login button at first position
        loginButton.isEnabled = false

        // toggle password
        var isPasswordVisible = false

        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_show) // closed eye
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_not_show) // open eye
            }
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        fun isLoginValid(): Boolean {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            return email.isNotEmpty() && password.isNotEmpty() && isEmailValid
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loginButton.isEnabled = isLoginValid()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        emailEditText.addTextChangedListener(watcher)
        passwordEditText.addTextChangedListener(watcher)

//        loginButton.setOnClickListener {
//            val intent = Intent(thiscom.example.gymnesia.LoginPage, WelcomPage::class.java)
//            startActivity(intent)
//        }

        loginTextView.setOnClickListener {
            val intent = Intent(this@LoginPage, RegisterPage::class.java)
            startActivity(intent)
            finish()
        }


    }

    //create user fun
    private fun createUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.createUser(user)
                withContext(Dispatchers.Main) {
//                    println("Create user status: ${response.code()}")
                    Toast.makeText(this@LoginPage, "inserted : $response", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}