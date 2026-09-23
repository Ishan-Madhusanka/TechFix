package com.techfix.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.techfix.app.admin.AdminDashboardActivity
import com.techfix.app.technician.TechnicianLoginActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var btnTechnicianLogin: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        btnTechnicianLogin = findViewById(R.id.btnTechnicianLogin)

        // Customer / Admin Login
        btnLogin.setOnClickListener {
            loginUser()
        }

        // Create Account
        tvRegister.setOnClickListener {
            val intent = Intent(
                this,
                RegisterActivity::class.java
            )
            startActivity(intent)
        }

        // Technician Login
        btnTechnicianLogin.setOnClickListener {
            val intent = Intent(
                this,
                TechnicianLoginActivity::class.java
            )
            startActivity(intent)
        }
    }

    private fun loginUser() {

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (email.isEmpty()) {
            etEmail.error = "Enter your email"
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Enter your password"
            return
        }

        btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val currentUser = auth.currentUser

                    if (currentUser == null) {
                        btnLogin.isEnabled = true

                        Toast.makeText(
                            this,
                            "User not found.",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@addOnCompleteListener
                    }

                    checkUserRole(currentUser.uid)

                } else {

                    btnLogin.isEnabled = true

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

    private fun checkUserRole(userId: String) {

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                btnLogin.isEnabled = true

                if (!document.exists()) {

                    Toast.makeText(
                        this,
                        "User profile not found.",
                        Toast.LENGTH_SHORT
                    ).show()

                    auth.signOut()
                    return@addOnSuccessListener
                }

                val role =
                    document.getString("role")
                        ?.trim()
                        ?.lowercase()
                        ?: "customer"

                if (role == "admin") {

                    Toast.makeText(
                        this,
                        "Admin login successful!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(
                        this,
                        AdminDashboardActivity::class.java
                    )

                    startActivity(intent)
                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Login successful!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(
                        this,
                        HomeActivity::class.java
                    )

                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener { error ->

                btnLogin.isEnabled = true

                Toast.makeText(
                    this,
                    "Failed to check user role: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()

                auth.signOut()
            }
    }
}
