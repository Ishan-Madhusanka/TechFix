package com.techfix.app.utils

import android.location.Location
import com.techfix.app.model.Branch

class LocationUtils {

    companion object {

        fun calculateDistance(
            userLatitude: Double,
            userLongitude: Double,
            branchLatitude: Double,
            branchLongitude: Double
        ): Float {

            val results = FloatArray(1)

            Location.distanceBetween(
                userLatitude,
                userLongitude,
                branchLatitude,
                branchLongitude,
                results
            )

            return results[0] / 1000f
        }

        fun findNearestBranch(
            userLatitude: Double,
            userLongitude: Double,
            branches: List<Branch>
        ): Branch? {

            return branches.minByOrNull { branch ->

                calculateDistance(
                    userLatitude,
                    userLongitude,
                    branch.latitude,
                    branch.longitude
                )
            }
        }
    }
}