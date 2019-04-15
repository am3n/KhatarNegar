package ir.am3n.khatarnegar

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.location.LocationManager
import android.util.Log


class GPSReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Meeeeee", "GPSReceiver > onReceive")
        when (intent?.action) {
            LocationManager.PROVIDERS_CHANGED_ACTION -> {
                try {
                    val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val gps = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) 1 else 0
                    val network = if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) 1 else 0
                    val sh = context.getSharedPreferences("settings", MODE_PRIVATE)
                    var lastGPSState = sh.getInt("lastGPSState", -1)
                    var lastNetworkState = sh.getInt("lastNetworkState", -1)
                    if (lastGPSState != gps || lastNetworkState != network) {
                        lastGPSState = gps
                        lastNetworkState = network
                        sh.edit().putInt("lastGPSState", lastGPSState).putInt("lastNetworkState", lastNetworkState).apply()
                        if (!isServiceRunning(context, LocationTrackerService::class.java)) {
                            Log.d("Meeeeee", "GPSReceiver > isServiceRunning : false")
                            // show a notifcation and add action to enable server
                            // that acton open mainActivity, so service started auto
                        } else
                            Log.d("Meeeeee", "GPSReceiver > isServiceRunning : true")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager?.getRunningServices(Integer.MAX_VALUE)!!) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

}
