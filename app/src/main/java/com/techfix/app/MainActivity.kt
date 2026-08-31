package com.techfix.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.LocationServices
import com.techfix.app.repository.BranchRepository

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Firestore branch test
        val branchRepository = BranchRepository()

        branchRepository.getBranches(
            onSuccess = { branches ->
                branches.forEach { branch ->
                    Log.d(
                        "TECHFIX_BRANCH",
                        "${branch.name} - ${branch.city} - ${branch.latitude}, ${branch.longitude}"
                    )
                }
            },
            onFailure = { exception ->
                Log.e(
                    "TECHFIX_BRANCH",
                    "Firestore error: ${exception.message}"
                )
            }
        )

        // GPS location
        checkLocationPermission()
    }

    private fun checkLocationPermission() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getCurrentLocation() {

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                if (location != null) {

                    Log.d(
                        "TECHFIX_LOCATION",
                        "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                    )

                } else {

                    Log.d(
                        "TECHFIX_LOCATION",
                        "Location is null"
                    )
                }
            }
            .addOnFailureListener { exception ->

                Log.e(
                    "TECHFIX_LOCATION",
                    "Location error: ${exception.message}"
                )
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (
            requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            Log.d(
                "TECHFIX_LOCATION",
                "Location permission denied"
            )
        }
    }
}