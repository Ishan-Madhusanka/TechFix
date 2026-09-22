package com.techfix.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Connect UI elements
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)

        // Login button
        btnLogin.setOnClickListener {
            loginUser()
        }

        // Create Account link
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        // Validation
        if (email.isEmpty()) {
            etEmail.error = "Enter your email"
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Enter your password"
            return
        }

        btnLogin.isEnabled = false

        // Firebase Login
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                btnLogin.isEnabled = true

                if (task.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Login successful!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Go to Home screen
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {

                    val errorMessage =
                        task.exception?.message ?: "Login failed"

                    Toast.makeText(
                        this,
                        "Login failed: $errorMessage",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}