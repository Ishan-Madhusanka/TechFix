package com.techfix.app.branch

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.techfix.app.R
import com.techfix.app.model.Branch
import com.techfix.app.repository.BranchRepository
import com.techfix.app.utils.LocationUtils

class BranchMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private lateinit var tvNearestBranchName: TextView
    private lateinit var tvNearestBranchCity: TextView
    private lateinit var tvNearestBranchDistance: TextView
    private lateinit var btnMyLocation: MaterialButton

    private val branchRepository = BranchRepository()

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private var branches: List<Branch> = emptyList()

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineLocationGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineLocationGranted || coarseLocationGranted) {

                enableLocationAndFindNearestBranch()

            } else {

                Toast.makeText(
                    this,
                    "Location permission is required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_branch_map)

        tvNearestBranchName =
            findViewById(R.id.tvNearestBranchName)

        tvNearestBranchCity =
            findViewById(R.id.tvNearestBranchCity)

        tvNearestBranchDistance =
            findViewById(R.id.tvNearestBranchDistance)

        btnMyLocation =
            findViewById(R.id.btnMyLocation)

        val mapFragment =
            supportFragmentManager.findFragmentById(
                R.id.map
            ) as SupportMapFragment

        mapFragment.getMapAsync(this)

        btnMyLocation.setOnClickListener {

            Toast.makeText(
                this,
                "Finding nearest branch...",
                Toast.LENGTH_SHORT
            ).show()

            checkLocationPermission()
        }
    }

    override fun onMapReady(map: GoogleMap) {

        googleMap = map

        loadBranches()
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

                googleMap.clear()

                branches.forEach { branch ->

                    val branchLocation =
                        LatLng(
                            branch.latitude,
                            branch.longitude
                        )

                    googleMap.addMarker(
                        MarkerOptions()
                            .position(branchLocation)
                            .title(branch.name)
                            .snippet(branch.city)
                    )
                }

                if (branches.isNotEmpty()) {

                    val firstBranch =
                        branches.first()

                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                firstBranch.latitude,
                                firstBranch.longitude
                            ),
                            8f
                        )
                    )
                }

                // Enable blue current-location dot if permission
                // already exists, but don't force nearest search.
                enableMyLocationOnly()
            },

            onFailure = { exception ->

                Toast.makeText(
                    this,
                    "Failed to load branches: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    // ---------------------------------------------------------
    // CHECK LOCATION PERMISSION
    // ---------------------------------------------------------

    private fun checkLocationPermission() {

        val fineLocationGranted =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {

            enableLocationAndFindNearestBranch()

        } else {

            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // ---------------------------------------------------------
    // ENABLE CURRENT LOCATION DOT ONLY
    // ---------------------------------------------------------

    private fun enableMyLocationOnly() {

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

        googleMap.isMyLocationEnabled = true
    }

    // ---------------------------------------------------------
    // FIND CURRENT GPS LOCATION + NEAREST BRANCH
    // ---------------------------------------------------------

    private fun enableLocationAndFindNearestBranch() {

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

        googleMap.isMyLocationEnabled = true

        if (branches.isEmpty()) {

            Toast.makeText(
                this,
                "No active branches available",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        fusedLocationClient
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            )
            .addOnSuccessListener { location ->

                if (location == null) {

                    Toast.makeText(
                        this,
                        "Current location not available. Please enable GPS.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                val userLatitude =
                    location.latitude

                val userLongitude =
                    location.longitude

                val nearestBranch =
                    LocationUtils.findNearestBranch(
                        userLatitude,
                        userLongitude,
                        branches
                    )

                if (nearestBranch == null) {

                    Toast.makeText(
                        this,
                        "Nearest branch not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                val distance =
                    LocationUtils.calculateDistance(
                        userLatitude,
                        userLongitude,
                        nearestBranch.latitude,
                        nearestBranch.longitude
                    )

                // Show nearest branch details
                tvNearestBranchName.text =
                    nearestBranch.name

                tvNearestBranchCity.text =
                    "City: ${nearestBranch.city}"

                tvNearestBranchDistance.text =
                    "Distance: %.2f km".format(distance)

                val nearestLocation =
                    LatLng(
                        nearestBranch.latitude,
                        nearestBranch.longitude
                    )

                // Zoom map to nearest branch
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        nearestLocation,
                        12f
                    )
                )

                Toast.makeText(
                    this,
                    "Nearest branch: ${nearestBranch.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Location error: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}