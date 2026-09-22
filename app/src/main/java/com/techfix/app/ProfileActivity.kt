package com.techfix.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        // Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Connect XML views
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        btnSave = findViewById(R.id.btnSave)

        // Load current profile
        loadProfile()

        // Save profile
        btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadProfile() {

        val user = auth.currentUser

        if (user == null) {

            Toast.makeText(
                this,
                "Please login first",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        // Firebase Authentication email
        etEmail.setText(user.email)

        // Get user name from Firestore
        firestore.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val name = document.getString("name")

                    etName.setText(name ?: "")
                }
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to load profile: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun saveProfile() {

        val user = auth.currentUser

        if (user == null) {

            Toast.makeText(
                this,
                "Please login first",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val name = etName.text
            .toString()
            .trim()

        // Validation
        if (name.isEmpty()) {

            etName.error = "Enter your name"
            etName.requestFocus()

            return
        }

        btnSave.isEnabled = false
        btnSave.text = "Saving..."

        // Update name in Firestore
        firestore.collection("users")
            .document(user.uid)
            .update(
                "name",
                name
            )
            .addOnSuccessListener {

                btnSave.isEnabled = true
                btnSave.text = "Save Profile"

                Toast.makeText(
                    this,
                    "Profile updated successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                // Go back to Home page
                val intent = Intent(
                    this,
                    HomeActivity::class.java
                )

                startActivity(intent)

                // Close ProfileActivity
                finish()
            }
            .addOnFailureListener { exception ->

                btnSave.isEnabled = true
                btnSave.text = "Save Profile"

                Toast.makeText(
                    this,
                    "Update failed: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}