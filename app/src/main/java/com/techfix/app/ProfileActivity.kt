package com.techfix.app

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

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        btnSave = findViewById(R.id.btnSave)

        loadProfile()

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

        etEmail.setText(user.email)

        firestore.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val name = document.getString("name")

                    etName.setText(name ?: "")
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Failed to load profile",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun saveProfile() {

        val user = auth.currentUser

        if (user == null) {
            return
        }

        val name = etName.text.toString().trim()

        if (name.isEmpty()) {
            etName.error = "Enter your name"
            return
        }

        btnSave.isEnabled = false

        firestore.collection("users")
            .document(user.uid)
            .update("name", name)
            .addOnSuccessListener {

                btnSave.isEnabled = true

                Toast.makeText(
                    this,
                    "Profile updated successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { exception ->

                btnSave.isEnabled = true

                Toast.makeText(
                    this,
                    "Update failed: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}