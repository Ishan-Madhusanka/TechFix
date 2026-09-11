package com.techfix.app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Connect UI elements
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

        // Validation
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

        // Create Firebase Authentication account
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val user = auth.currentUser

                    if (user != null) {

                        val userId = user.uid

                        // User data for Firestore
                        val userData = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "role" to "CUSTOMER",
                            "createdAt" to com.google.firebase.Timestamp.now()
                        )

                        // Save user data in Firestore
                        firestore.collection("users")
                            .document(userId)
                            .set(userData)
                            .addOnSuccessListener {

                                btnRegister.isEnabled = true

                                Toast.makeText(
                                    this,
                                    "Registration successful!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                Log.d(
                                    "TECHFIX_FIRESTORE",
                                    "User saved successfully: $userId"
                                )
                            }
                            .addOnFailureListener { exception ->

                                btnRegister.isEnabled = true

                                Toast.makeText(
                                    this,
                                    "Account created, but profile save failed.",
                                    Toast.LENGTH_LONG
                                ).show()

                                Log.e(
                                    "TECHFIX_FIRESTORE",
                                    "Failed to save user: ${exception.message}",
                                    exception
                                )
                            }

                    }

                } else {

                    btnRegister.isEnabled = true

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