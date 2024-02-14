package io.github.auag0.hidemocklocation.app.utils

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import io.github.auag0.hidemocklocation.app.utils.LocationUtils.GetMethod.GET_CURRENT_LOCATION
import io.github.auag0.hidemocklocation.app.utils.LocationUtils.GetMethod.GET_LAST_KNOWN_LOCATION
import io.github.auag0.hidemocklocation.app.utils.LocationUtils.GetMethod.REQUEST_SINGLE_UPDATE
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationUtils(
    private val context: Context
) {
    private val locationManager: LocationManager by lazy {
        context.getSystemService(LocationManager::class.java)!!
    }

    enum class GetMethod {
        GET_CURRENT_LOCATION, REQUEST_SINGLE_UPDATE, GET_LAST_KNOWN_LOCATION
    }

    companion object {
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getLocationAsync(
        method: GetMethod,
        callback: (location: Location?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val location = getLocation(method)
            callback(location)
        }
    }

    private suspend fun getLocation(method: GetMethod): Location? {
        return when (method) {
            GET_CURRENT_LOCATION -> getCurrentLocation()
            REQUEST_SINGLE_UPDATE -> requestSingleUpdate()
            GET_LAST_KNOWN_LOCATION -> getLastKnownLocation()
        }
    }

    private suspend fun getCurrentLocation(): Location? {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            return null
        return suspendCoroutine { continuation ->
            try {
                locationManager.getCurrentLocation(
                    LocationManager.GPS_PROVIDER,
                    null,
                    context.mainExecutor
                ) { location ->
                    continuation.resume(location)
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                continuation.resume(null)
            }
        }
    }

    private suspend fun requestSingleUpdate(): Location? {
        return suspendCoroutine { continuation ->
            try {
                @Suppress("DEPRECATION")
                locationManager.requestSingleUpdate(
                    LocationManager.GPS_PROVIDER, { location ->
                        continuation.resume(location)
                    }, null
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
                continuation.resume(null)
            }
        }
    }

    private fun getLastKnownLocation(): Location? {
        return try {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } catch (e: SecurityException) {
            e.printStackTrace()
            null
        }
    }
}