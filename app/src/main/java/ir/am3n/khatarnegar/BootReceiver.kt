package ir.am3n.khatarnegar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {

                /* todo notify user that
                if (!PermissionRequest.checkIfAlreadyhaveLocationPermission(this)) {
                    PermissionRequest.requestForLocationPermission(this)
                    return
                }
                 */

                val serviceIntent = Intent(context, LocationTrackerService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    context?.startForegroundService(serviceIntent)
                else
                    context?.startService(serviceIntent)
            }
        }
    }

}
