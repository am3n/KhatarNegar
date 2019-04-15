package ir.am3n.khatarnegar

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var locationTrackerService: LocationTrackerService? = null
    private var serviceConnection: ServiceConnection? = null
    private var locationTrackerServiceIsBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startLocationTracker()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionRequest.LOCATION_PERMISSION_REQUEST_CODE -> {
                startLocationTracker()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceConnection?.let { unbindService(it) }
    }

    private fun startLocationTracker() {

        if (!PermissionRequest.checkIfAlreadyhaveLocationPermission(this)) {
            PermissionRequest.requestForLocationPermission(this)
            return
        }

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                val binder = service as LocationTrackerService.MyLocalBinder
                locationTrackerService = binder.getService()
                locationTrackerServiceIsBound = true
            }
            override fun onServiceDisconnected(name: ComponentName) {
                locationTrackerServiceIsBound = false
            }
        }

        val intent = Intent(this, LocationTrackerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intent)
        else
            startService(intent)
        serviceConnection?.let { bindService(intent, it, Context.BIND_AUTO_CREATE) }

    }

}

