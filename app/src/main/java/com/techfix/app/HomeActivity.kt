package com.techfix.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    // These are clickable card layouts in activity_home.xml
    private lateinit var btnServices: View
    private lateinit var btnMyBookings: View
    private lateinit var btnProfile: View
    private lateinit var btnLogout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        // Connect UI elements
        btnServices = findViewById(R.id.btnServices)
        btnMyBookings = findViewById(R.id.btnMyBookings)
        btnProfile = findViewById(R.id.btnProfile)
        btnLogout = findViewById(R.id.btnLogout)

        // Repair Services
        btnServices.setOnClickListener {

            val intent = Intent(
                this,
                RepairServicesActivity::class.java
            )

            startActivity(intent)
        }

        // My Bookings
        btnMyBookings.setOnClickListener {

            val intent = Intent(
                this,
                MyBookingsActivity::class.java
            )

            startActivity(intent)
        }

        // My Profile
        btnProfile.setOnClickListener {

            val intent = Intent(
                this,
                ProfileActivity::class.java
            )

            startActivity(intent)
        }

        // Logout
        btnLogout.setOnClickListener {

            FirebaseAuth
                .getInstance()
                .signOut()

            Toast.makeText(
                this,
                "Logged out successfully!",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            startActivity(intent)

            finishAffinity()
        }
    }
}