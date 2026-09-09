package com.techfix.app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)

        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {

        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (name.isEmpty()) {
            etName.error = "Enter your name"
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Enter your email"
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Enter a password"
            return
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            return
        }

        btnRegister.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                btnRegister.isEnabled = true

                if (task.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Registration successful!",
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.d(
                        "TECHFIX_AUTH",
                        "SUCCESS - User created: ${auth.currentUser?.uid}"
                    )

                } else {

                    val errorMessage =
                        task.exception?.message ?: "Unknown error"

                    Toast.makeText(
                        this,
                        "Registration failed: $errorMessage",
                        Toast.LENGTH_LONG
                    ).show()

                    Log.e(
                        "TECHFIX_AUTH",
                        "FAILED - $errorMessage",
                        task.exception
                    )
                }
            }
    }
}