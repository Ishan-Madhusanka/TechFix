package com.techfix.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.techfix.app.branch.BookingConfirmationActivity
import com.techfix.app.model.Branch
import com.techfix.app.repository.BranchRepository
import com.techfix.app.repository.ServiceRepository
import com.techfix.app.repository.SparePartRepository
import com.techfix.app.repository.TechnicianRepository
import com.techfix.app.utils.LocationUtils

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private val branchRepository = BranchRepository()
    private val serviceRepository = ServiceRepository()
    private val sparePartRepository = SparePartRepository()
    private val technicianRepository = TechnicianRepository()

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private var branches: List<Branch> = emptyList()

    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    private var selectedServiceId: String? = null
    private var requiredPartId: String = ""

    private var serviceLoaded = false
    private var locationLoaded = false
    private var branchesLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        /*
         * TEMPORARY TEST
         *
         * We are testing the Windows Installation service.
         * Firestore document ID:
         * desktop_os_install
         *
         * IMPORTANT:
         * After testing, restore this line to:
         *
         * selectedServiceId = intent.getStringExtra("SERVICE_ID")
         */
        selectedServiceId = intent.getStringExtra("SERVICE_ID")

        loadBranches()
        loadSelectedService()
        checkLocationPermission()
    }

    // ---------------------------------------------------------
    // LOAD SELECTED SERVICE
    // ---------------------------------------------------------

    private fun loadSelectedService() {

        val serviceId = selectedServiceId

        if (serviceId.isNullOrEmpty()) {

            Log.e(
                "TECHFIX_SERVICE",
                "No SERVICE_ID received"
            )

            Toast.makeText(
                this,
                "Service not selected",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        Log.d(
            "TECHFIX_SERVICE",
            "Loading service: $serviceId"
        )

        serviceRepository.getServiceById(

            serviceId = serviceId,

            onSuccess = { service ->

                Log.d(
                    "TECHFIX_TEST",
                    "Service loaded = ${service?.name}"
                )

                if (service == null) {

                    Log.e(
                        "TECHFIX_SERVICE",
                        "Service not found: $serviceId"
                    )

                    Toast.makeText(
                        this,
                        "Service not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@getServiceById
                }

                requiredPartId = service.requiredPartId.trim()

                serviceLoaded = true

                Log.d(
                    "TECHFIX_SERVICE",
                    "Service: ${service.name}"
                )

                Log.d(
                    "TECHFIX_SERVICE",
                    "Required Part ID: '$requiredPartId'"
                )

                if (requiredPartId.isEmpty()) {

                    Log.d(
                        "TECHFIX_SERVICE",
                        "No spare part required for this service"
                    )
                }

                tryToFindSuitableBranch()
            },

            onFailure = { exception ->

                Log.e(
                    "TECHFIX_SERVICE",
                    "Service loading failed: ${exception.message}",
                    exception
                )

                Toast.makeText(
                    this,
                    "Failed to load service",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    // ---------------------------------------------------------
    // LOAD ACTIVE BRANCHES
    // ---------------------------------------------------------

    private fun loadBranches() {

        branchRepository.getBranches(

            onSuccess = { branchList ->

                branches = branchList.filter {
                    it.isActive
                }

                branchesLoaded = true

                Log.d(
                    "TECHFIX_BRANCH",
                    "Active branches found: ${branches.size}"
                )

                branches.forEach { branch ->

                    Log.d(
                        "TECHFIX_BRANCH",
                        "${branch.name} | ${branch.city} | ${branch.latitude}, ${branch.longitude}"
                    )
                }

                tryToFindSuitableBranch()
            },

            onFailure = { exception ->

                Log.e(
                    "TECHFIX_BRANCH",
                    "Branch loading failed: ${exception.message}",
                    exception
                )

                Toast.makeText(
                    this,
                    "Failed to load branches",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    // ---------------------------------------------------------
    // LOCATION PERMISSION
    // ---------------------------------------------------------

    private fun checkLocationPermission() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
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

    // ---------------------------------------------------------
    // GET CURRENT GPS LOCATION
    // ---------------------------------------------------------

    private fun getCurrentLocation() {

        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        Log.d(
            "TECHFIX_LOCATION",
            "Requesting current location..."
        )

        fusedLocationClient
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            )
            .addOnSuccessListener { location ->

                if (location == null) {

                    Log.e(
                        "TECHFIX_LOCATION",
                        "Current location is null"
                    )

                    Toast.makeText(
                        this,
                        "Unable to get current location",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                currentLatitude = location.latitude
                currentLongitude = location.longitude

                locationLoaded = true

                Log.d(
                    "TECHFIX_LOCATION",
                    "Latitude = ${location.latitude}"
                )

                Log.d(
                    "TECHFIX_LOCATION",
                    "Longitude = ${location.longitude}"
                )

                tryToFindSuitableBranch()
            }

            .addOnFailureListener { exception ->

                Log.e(
                    "TECHFIX_LOCATION",
                    "Location error: ${exception.message}",
                    exception
                )

                Toast.makeText(
                    this,
                    "Location error",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // ---------------------------------------------------------
    // PERMISSION RESULT
    // ---------------------------------------------------------

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

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (
                grantResults.isNotEmpty() &&
                grantResults.any {
                    it == PackageManager.PERMISSION_GRANTED
                }
            ) {

                getCurrentLocation()

            } else {

                Toast.makeText(
                    this,
                    "Location permission is required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ---------------------------------------------------------
    // WAIT UNTIL EVERYTHING IS READY
    // ---------------------------------------------------------

    private fun tryToFindSuitableBranch() {

        Log.d(
            "TECHFIX_FLOW",
            "Ready status -> Service=$serviceLoaded, Branches=$branchesLoaded, Location=$locationLoaded"
        )

        if (!serviceLoaded) {
            return
        }

        if (!branchesLoaded) {
            return
        }

        if (!locationLoaded) {
            return
        }

        if (branches.isEmpty()) {

            Toast.makeText(
                this,
                "No active branches available",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        calculateBranchDistances()
    }

    // ---------------------------------------------------------
    // CALCULATE DISTANCES
    // ---------------------------------------------------------

    private fun calculateBranchDistances() {

        val userLatitude = currentLatitude ?: return
        val userLongitude = currentLongitude ?: return

        val sortedBranches = branches.sortedBy { branch ->

            val distance = LocationUtils.calculateDistance(
                userLatitude,
                userLongitude,
                branch.latitude,
                branch.longitude
            )

            Log.d(
                "TECHFIX_DISTANCE",
                "${branch.name} distance = ${"%.2f".format(distance)} km"
            )

            distance
        }

        if (sortedBranches.isEmpty()) {

            Toast.makeText(
                this,
                "No branches available",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        Log.d(
            "TECHFIX_FLOW",
            "Starting suitable branch search"
        )

        findSuitableBranch(
            sortedBranches = sortedBranches,
            index = 0
        )
    }

    // ---------------------------------------------------------
    // FIND SUITABLE BRANCH
    // ---------------------------------------------------------

    private fun findSuitableBranch(
        sortedBranches: List<Branch>,
        index: Int
    ) {

        if (index >= sortedBranches.size) {

            Log.d(
                "TECHFIX_FLOW",
                "No suitable branch found"
            )

            Toast.makeText(
                this,
                "No suitable branch available",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val branch = sortedBranches[index]

        Log.d(
            "TECHFIX_FLOW",
            "Checking branch: ${branch.name}"
        )

        /*
         * If requiredPartId is empty,
         * this service does NOT need a spare part.
         *
         * Therefore skip the spare-part check
         * and directly check technician availability.
         */

        if (requiredPartId.isEmpty()) {

            Log.d(
                "TECHFIX_SPARE_QUERY",
                "${branch.name}: No spare part required - skipping spare part check"
            )

            checkTechnicianAvailability(
                branch = branch,
                sortedBranches = sortedBranches,
                index = index,
                requiredPartName = "Not Required"
            )

            return
        }

        /*
         * Service needs a specific spare part.
         */

        Log.d(
            "TECHFIX_SPARE_QUERY",
            "Checking required part '$requiredPartId' at branch ${branch.id}"
        )

        sparePartRepository.getRequiredSparePartByBranch(

            branchId = branch.id,
            requiredPartId = requiredPartId,

            onSuccess = { sparePart ->

                if (sparePart != null) {

                    Log.d(
                        "TECHFIX_SPARE_QUERY",
                        "Required part found: ${sparePart.name}"
                    )

                    checkTechnicianAvailability(
                        branch = branch,
                        sortedBranches = sortedBranches,
                        index = index,
                        requiredPartName = sparePart.name
                    )

                } else {

                    Log.d(
                        "TECHFIX_SPARE_QUERY",
                        "Required part not available at ${branch.name}"
                    )

                    findSuitableBranch(
                        sortedBranches,
                        index + 1
                    )
                }
            },

            onFailure = { exception ->

                Log.e(
                    "TECHFIX_SPARE_QUERY",
                    "Spare part query failed: ${exception.message}",
                    exception
                )

                findSuitableBranch(
                    sortedBranches,
                    index + 1
                )
            }
        )
    }

    // ---------------------------------------------------------
    // CHECK TECHNICIAN
    // ---------------------------------------------------------

    private fun checkTechnicianAvailability(
        branch: Branch,
        sortedBranches: List<Branch>,
        index: Int,
        requiredPartName: String
    ) {

        Log.d(
            "TECHFIX_TECH_QUERY",
            "Checking technicians at ${branch.name}"
        )

        technicianRepository.getAvailableTechniciansByBranch(

            branchId = branch.id,

            onSuccess = { technicians ->

                Log.d(
                    "TECHFIX_TECH_QUERY",
                    "${branch.name} -> ${technicians.size} available technicians"
                )

                if (technicians.isNotEmpty()) {

                    val selectedTechnician = technicians.first()

                    val latitude = currentLatitude
                    val longitude = currentLongitude

                    val distance =
                        if (latitude != null && longitude != null) {

                            LocationUtils.calculateDistance(
                                latitude,
                                longitude,
                                branch.latitude,
                                branch.longitude
                            )

                        } else {
                            0f
                        }

                    Log.d(
                        "TECHFIX_RESULT",
                        "Suitable Branch: ${branch.name}"
                    )

                    Log.d(
                        "TECHFIX_RESULT",
                        "Branch ID: ${branch.id}"
                    )

                    Log.d(
                        "TECHFIX_RESULT",
                        "Distance: ${"%.2f".format(distance)} km"
                    )

                    Log.d(
                        "TECHFIX_RESULT",
                        "Required Part: $requiredPartName"
                    )

                    Log.d(
                        "TECHFIX_RESULT",
                        "Technician: ${selectedTechnician.name} | Speciality: ${selectedTechnician.speciality}"
                    )

                    openBookingConfirmation(
                        branch = branch,
                        technicianId = selectedTechnician.id,
                        technicianName = selectedTechnician.name
                    )

                } else {

                    Log.d(
                        "TECHFIX_TECH_QUERY",
                        "No available technician at ${branch.name}. Checking next branch."
                    )

                    findSuitableBranch(
                        sortedBranches,
                        index + 1
                    )
                }
            },

            onFailure = { exception ->

                Log.e(
                    "TECHFIX_TECH_QUERY",
                    "Technician query failed: ${exception.message}",
                    exception
                )

                findSuitableBranch(
                    sortedBranches,
                    index + 1
                )
            }
        )
    }

    // ---------------------------------------------------------
    // OPEN BOOKING CONFIRMATION
    // ---------------------------------------------------------

    private fun openBookingConfirmation(
        branch: Branch,
        technicianId: String,
        technicianName: String
    ) {

        val serviceId = selectedServiceId ?: return

        Log.d(
            "TECHFIX_FLOW",
            "Opening BookingConfirmationActivity"
        )

        val confirmationIntent =
            Intent(
                this,
                BookingConfirmationActivity::class.java
            )

        confirmationIntent.putExtra(
            "SERVICE_ID",
            serviceId
        )

        confirmationIntent.putExtra(
            "BRANCH_ID",
            branch.id
        )

        confirmationIntent.putExtra(
            "BRANCH_NAME",
            branch.name
        )

        confirmationIntent.putExtra(
            "TECHNICIAN_ID",
            technicianId
        )

        confirmationIntent.putExtra(
            "TECHNICIAN_NAME",
            technicianName
        )

        startActivity(confirmationIntent)
    }
}