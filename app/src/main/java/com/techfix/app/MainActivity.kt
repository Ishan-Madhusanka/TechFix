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
import com.techfix.app.repository.BranchRepository
import com.techfix.app.repository.SparePartRepository
import com.techfix.app.repository.TechnicianRepository
import com.techfix.app.utils.LocationUtils

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    private var branches: List<Branch> = emptyList()

    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        loadBranches()

        checkLocationPermission()
    }

    private fun loadBranches() {

        val branchRepository = BranchRepository()

        branchRepository.getBranches(

            onSuccess = { branchList ->

                branches = branchList

                branches.forEach { branch ->

                    Log.d(
                        "TECHFIX_BRANCH",
                        "${branch.name} - ${branch.city} - " +
                                "${branch.latitude}, ${branch.longitude}"
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
                        "Latitude: ${location.latitude}, " +
                                "Longitude: ${location.longitude}"
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

        // Calculate distance for every branch
        branches.forEach { branch ->

            val distance =
                LocationUtils.calculateDistance(
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

        // Sort branches from nearest to farthest
        val sortedBranches =
            branches.sortedBy { branch ->

                LocationUtils.calculateDistance(
                    userLatitude = userLat,
                    userLongitude = userLng,
                    branchLatitude = branch.latitude,
                    branchLongitude = branch.longitude
                )
            }

        // Start checking available resources
        findSuitableBranch(
            sortedBranches = sortedBranches,
            index = 0
        )
    }

    private fun findSuitableBranch(
        sortedBranches: List<Branch>,
        index: Int
    ) {

        // All branches checked
        if (index >= sortedBranches.size) {

            Log.d(
                "TECHFIX_SUITABLE_BRANCH",
                "No suitable branch found"
            )

            return
        }

        val branch = sortedBranches[index]

        val userLat = currentLatitude
        val userLng = currentLongitude

        Log.d(
            "TECHFIX_SUITABLE_BRANCH",
            "Checking branch: ${branch.name}"
        )

        // Display distance of currently checked branch
        if (
            userLat != null &&
            userLng != null
        ) {

            val distance =
                LocationUtils.calculateDistance(
                    userLatitude = userLat,
                    userLongitude = userLng,
                    branchLatitude = branch.latitude,
                    branchLongitude = branch.longitude
                )

            Log.d(
                "TECHFIX_SUITABLE_BRANCH",
                "${branch.name} distance = %.2f km".format(distance)
            )
        }

        val sparePartRepository =
            SparePartRepository()

        val technicianRepository =
            TechnicianRepository()

        // First check spare parts
        sparePartRepository.getAvailableSparePartsByBranch(

            branchId = branch.id,

            onSuccess = { spareParts ->

                Log.d(
                    "TECHFIX_SPARE_QUERY",
                    "${branch.name}: Spare parts found = ${spareParts.size}"
                )

                if (spareParts.isNotEmpty()) {

                    // Spare parts available
                    // Now check technicians

                    technicianRepository.getAvailableTechniciansByBranch(

                        branchId = branch.id,

                        onSuccess = { technicians ->

                            Log.d(
                                "TECHFIX_TECH_QUERY",
                                "${branch.name}: Available technicians = ${technicians.size}"
                            )

                            if (technicians.isNotEmpty()) {

                                // Suitable branch found
                                Log.d(
                                    "TECHFIX_SUITABLE_BRANCH",
                                    "Suitable Branch: ${branch.name}"
                                )

                                Log.d(
                                    "TECHFIX_SUITABLE_BRANCH",
                                    "Branch ID: ${branch.id}"
                                )

                                Log.d(
                                    "TECHFIX_SUITABLE_BRANCH",
                                    "Spare Parts: ${spareParts.size}"
                                )

                                Log.d(
                                    "TECHFIX_SUITABLE_BRANCH",
                                    "Technicians: ${technicians.size}"
                                )

                                // Show available spare parts
                                spareParts.forEach { sparePart ->

                                    Log.d(
                                        "TECHFIX_SUITABLE_BRANCH",
                                        "Spare Part: ${sparePart.name} | " +
                                                "Qty: ${sparePart.quantity} | " +
                                                "Price: ${sparePart.price}"
                                    )
                                }

                                // Show available technicians
                                technicians.forEach { technician ->

                                    Log.d(
                                        "TECHFIX_SUITABLE_BRANCH",
                                        "Technician: ${technician.name} | " +
                                                "Specialization: ${technician.specialization}"
                                    )
                                }

                            } else {

                                // Technician unavailable
                                // Check next nearest branch

                                Log.d(
                                    "TECHFIX_SUITABLE_BRANCH",
                                    "${branch.name}: No available technicians"
                                )

                                findSuitableBranch(
                                    sortedBranches = sortedBranches,
                                    index = index + 1
                                )
                            }
                        },

                        onFailure = { exception ->

                            Log.e(
                                "TECHFIX_SUITABLE_BRANCH",
                                "Technician error: ${exception.message}"
                            )
                        }
                    )

                } else {

                    // Spare parts unavailable
                    // Check next nearest branch

                    Log.d(
                        "TECHFIX_SUITABLE_BRANCH",
                        "${branch.name}: No available spare parts"
                    )

                    findSuitableBranch(
                        sortedBranches = sortedBranches,
                        index = index + 1
                    )
                }
            },

            onFailure = { exception ->

                Log.e(
                    "TECHFIX_SUITABLE_BRANCH",
                    "Spare part error: ${exception.message}"
                )
            }
        )
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
            grantResults[0] ==
            PackageManager.PERMISSION_GRANTED
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