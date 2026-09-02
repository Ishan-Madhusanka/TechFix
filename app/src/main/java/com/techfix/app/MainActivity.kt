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
import com.techfix.app.model.Branch
import com.techfix.app.model.SparePart
import com.techfix.app.repository.BranchRepository
import com.techfix.app.utils.LocationUtils

import com.techfix.app.repository.SparePartRepository

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    private var branches: List<Branch> = emptyList()

    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val branchRepository = BranchRepository()

        branchRepository.getBranches(
            onSuccess = { branchList ->

                branches = branchList

                branches.forEach { branch ->
                    Log.d(
                        "TECHFIX_BRANCH",
                        "${branch.name} - ${branch.city} - ${branch.latitude}, ${branch.longitude}"
                    )
                }

                calculateBranchDistances()
            },

            onFailure = { exception ->

                Log.e(
                    "TECHFIX_BRANCH",
                    "Firestore error: ${exception.message}"
                )
            }
        )

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

        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            null
        )
            .addOnSuccessListener { location ->

                if (location != null) {

                    currentLatitude = location.latitude
                    currentLongitude = location.longitude

                    Log.d(
                        "TECHFIX_LOCATION",
                        "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                    )

                    calculateBranchDistances()

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

    private fun calculateBranchDistances() {

        val userLat = currentLatitude
        val userLng = currentLongitude

        if (
            userLat == null ||
            userLng == null ||
            branches.isEmpty()
        ) {
            return
        }

        branches.forEach { branch ->

            val distance = LocationUtils.calculateDistance(
                userLatitude = userLat,
                userLongitude = userLng,
                branchLatitude = branch.latitude,
                branchLongitude = branch.longitude
            )

            Log.d(
                "TECHFIX_DISTANCE",
                "${branch.name} = %.2f km".format(distance)
            )
        }

        val nearestBranch = LocationUtils.findNearestBranch(
            userLatitude = userLat,
            userLongitude = userLng,
            branches = branches
        )

        if (nearestBranch != null) {

            val nearestDistance = LocationUtils.calculateDistance(
                userLatitude = userLat,
                userLongitude = userLng,
                branchLatitude = nearestBranch.latitude,
                branchLongitude = nearestBranch.longitude
            )

            Log.d(
                "TECHFIX_NEAREST",
                "Nearest Branch: ${nearestBranch.name} - %.2f km".format(nearestDistance)
            )

            val sparePartRepository = SparePartRepository()

            Log.d(
                "TECHFIX_SPARE_QUERY",
                "Checking spare parts for branchId: ${nearestBranch.id}"
            )
            sparePartRepository.getAvailableSparePartsByBranch(
                branchId = nearestBranch.id,

                onSuccess = { spareParts ->

                    Log.d(
                        "TECHFIX_SPARE_QUERY",
                        "Documents found: ${spareParts.size}"
                    )

                    spareParts.forEach { sparepart ->

                        Log.d(
                            "TECHFIX_SPARE_QUERY",
                            "${sparepart.name} - Qty: ${sparepart.quantity} - price: ${sparepart.price}"
                        )
                    }
                },

                onFailure = { exception ->

                    Log.e(
                        "TECHFIX_SPARE_QUERY",
                        "Firestore error: ${exception.message}"
                    )
                }
            )
        }



        fun onRequestPermissionsResult(
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
}