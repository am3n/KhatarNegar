package ir.am3n.khatarnegar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*


class LocationTracker(context: Context) {

    private var context: Context? = context
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private val interval: Long = 5*60*1000
    private val fastestInterval: Long = 60*1000

    init {
        if (isGooglePlayServicesAvailable()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            createLocationRequest()
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    for (location in locationResult?.locations!!) {
                        val log = "%.8f".format(location.latitude) + ", " + "%.8f".format(location.longitude)
                        Log.d("Meeeeee","LocationTracker > $log")
                    }
                }
            }
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest?.interval = interval
        locationRequest?.fastestInterval = fastestInterval
        locationRequest?.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)
        return resultCode == ConnectionResult.SUCCESS
    }

    @SuppressLint("MissingPermission")
    fun start() : LocationTracker {
        // check permissions and gps
        Log.d("Meeeeee", "LocationTracker > start")
        fusedLocationClient?.removeLocationUpdates(locationCallback)
        // handler ? null or myLooper or mainLooper ???
        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        return this
    }

    fun pause() {
        Log.d("Meeeeee", "LocationTracker > pause")
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

}