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
import com.techfix.app.repository.ServiceRepository
import com.techfix.app.repository.SparePartRepository
import com.techfix.app.repository.TechnicianRepository
import com.techfix.app.utils.LocationUtils
import android.content.Intent
import com.techfix.app.branch.BookingConfirmationActivity
class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    private var branches: List<Branch> = emptyList()

    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    private var selectedServiceId: String? = null
    private var requiredPartId: String? = null

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

        selectedServiceId = intent.getStringExtra("SERVICE_ID")

        selectedServiceId?.let { serviceId ->
            loadRequiredPartForService(serviceId)
        }

        loadBranches()

        checkLocationPermission()
    }


     // Load active TechFix branches

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

    /*
     * Check runtime location permission
     */
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

    /*
     * Get current GPS location
     */
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

    /*
     * Load selected service and get requiredPartId
     */
    private fun loadRequiredPartForService(serviceId: String) {

        val serviceRepository = ServiceRepository()

        serviceRepository.getServiceById(

            serviceId = serviceId,

            onSuccess = { service ->

                if (service != null) {

                    requiredPartId = service.requiredPartId

                    Log.d(
                        "TECHFIX_SERVICE",
                        "Service: ${service.name} | " +
                                "Required Part ID: ${service.requiredPartId}"
                    )

                    calculateBranchDistances()

                } else {

                    Log.d(
                        "TECHFIX_SERVICE",
                        "Service not found"
                    )
                }
            },

            onFailure = { exception ->

                Log.e(
                    "TECHFIX_SERVICE",
                    "Service error: ${exception.message}"
                )
            }
        )
    }

    /*
     * Calculate distance from user to branches
     * and sort nearest -> farthest
     */
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

        val sortedBranches =
            branches.sortedBy { branch ->

                LocationUtils.calculateDistance(
                    userLatitude = userLat,
                    userLongitude = userLng,
                    branchLatitude = branch.latitude,
                    branchLongitude = branch.longitude
                )
            }

        findSuitableBranch(
            sortedBranches = sortedBranches,
            index = 0
        )
    }

    /*
     * Find nearest suitable branch
     *
     * Checks:
     * 1. Exact required spare part (if required)
     * 2. Spare part available
     * 3. Quantity > 0
     * 4. Technician available
     */
    private fun findSuitableBranch(
        sortedBranches: List<Branch>,
        index: Int
    ) {

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

        val technicianRepository =
            TechnicianRepository()

        val partId = requiredPartId

        /*
         * CASE 1
         * Service does not require a spare part
         */
        if (partId.isNullOrBlank()) {

            Log.d(
                "TECHFIX_SPARE_QUERY",
                "${branch.name}: Service does not require a spare part"
            )

            checkTechnicianAvailability(
                branch = branch,
                sortedBranches = sortedBranches,
                index = index,
                technicianRepository = technicianRepository,
                requiredPartName = null
            )

            return
        }

        /*
         * CASE 2
         * Service requires an exact spare part
         */
        val sparePartRepository =
            SparePartRepository()

        sparePartRepository.getRequiredSparePartByBranch(

            branchId = branch.id,
            requiredPartId = partId,

            onSuccess = { sparePart ->

                if (sparePart != null) {

                    Log.d(
                        "TECHFIX_SPARE_QUERY",
                        "${branch.name}: Required spare part available"
                    )

                    Log.d(
                        "TECHFIX_SPARE_QUERY",
                        "Part: ${sparePart.name} | " +
                                "Qty: ${sparePart.quantity} | " +
                                "Price: ${sparePart.price}"
                    )

                    checkTechnicianAvailability(
                        branch = branch,
                        sortedBranches = sortedBranches,
                        index = index,
                        technicianRepository = technicianRepository,
                        requiredPartName = sparePart.name
                    )

                } else {

                    Log.d(
                        "TECHFIX_SPARE_QUERY",
                        "${branch.name}: Required spare part $partId unavailable"
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

    /*
     * Check technician availability
     */
    private fun checkTechnicianAvailability(
        branch: Branch,
        sortedBranches: List<Branch>,
        index: Int,
        technicianRepository: TechnicianRepository,
        requiredPartName: String?
    ) {

        technicianRepository.getAvailableTechniciansByBranch(

            branchId = branch.id,

            onSuccess = { technicians ->

                Log.d(
                    "TECHFIX_TECH_QUERY",
                    "${branch.name}: Available technicians = ${technicians.size}"
                )

                if (technicians.isNotEmpty()) {

                    Log.d(
                        "TECHFIX_SUITABLE_BRANCH",
                        "Suitable Branch: ${branch.name}"
                    )

                    Log.d(
                        "TECHFIX_SUITABLE_BRANCH",
                        "Branch ID: ${branch.id}"
                    )

                    if (requiredPartName != null) {

                        Log.d(
                            "TECHFIX_SUITABLE_BRANCH",
                            "Required Part: $requiredPartName"
                        )

                        val selectedTechnician = technicians.first()

                        val confirmationIntent =
                            Intent(this@MainActivity, BookingConfirmationActivity::class.java)

                        confirmationIntent.putExtra("SERVICE_ID", selectedServiceId)
                        confirmationIntent.putExtra("BRANCH_ID", branch.id)
                        confirmationIntent.putExtra("BRANCH_NAME", branch.name)
                        confirmationIntent.putExtra("TECHNICIAN_ID", selectedTechnician.id)
                        confirmationIntent.putExtra("TECHNICIAN_NAME", selectedTechnician.name)

                        startActivity(confirmationIntent)
                    }

                    else {

                        Log.d(
                            "TECHFIX_SUITABLE_BRANCH",
                            "Required Part: Not required"
                        )
                    }

                    technicians.forEach { technician ->

                        Log.d(
                            "TECHFIX_SUITABLE_BRANCH",
                            "Technician: ${technician.name} | " +
                                    "Speciality: ${technician.speciality}"
                        )
                    }

                } else {

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
    }

    /*
     * Location permission result
     */
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