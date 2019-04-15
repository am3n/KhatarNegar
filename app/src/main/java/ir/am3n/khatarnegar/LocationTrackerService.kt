package ir.am3n.khatarnegar

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_MIN
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class LocationTrackerService : Service() {

    private val myBinder = MyLocalBinder()
    private var locationTracker: LocationTracker? = null
    private var gpsReceiver: BroadcastReceiver? = null
    private val notificationId = 75137575
    private val channelId = "chnl01"
    private var channelName = "chnlocation"

    override fun onBind(intent: Intent): IBinder? {
        Log.d("Meeeeee", "LocationTrackerService > onBind")
        return myBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Meeeeee", "LocationTrackerService > onStartCommand")
        return START_STICKY
    }

    override fun onCreate() {

        Log.d("Meeeeee", "LocationTrackerService > onCreate")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel()
        startForeground(notificationId, getNotification(true))

        locationTracker = LocationTracker(this)

        gpsReceiver = object : BroadcastReceiver() {
            private var lastGPSState: Int = -1
            private var lastNetworkState: Int = -1
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("Meeeeee", "LocationTrackerService > receiver > onReceive")
                when (intent?.action) {
                    LocationManager.PROVIDERS_CHANGED_ACTION -> {
                        try {
                            val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            val gps = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) 1 else 0
                            val network = if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) 1 else 0

                            Log.d("Meeeeee", "LocationTrackerService > gps:$gps & net:$network")

                            if (lastGPSState != gps) {
                                lastGPSState = gps
                                onGPSStateChanged(gps==1)
                            }
                            if (lastNetworkState != network) {
                                lastNetworkState = network
                                onNetworkStateChanged(network==1)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            fun onGPSStateChanged(enabled: Boolean) {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(notificationId, getNotification(enabled))
                if (enabled) {
                    locationTracker?.start()
                    // alert user in notification
                } else {
                    locationTracker?.pause()
                    // alert user in notification
                }
            }
            fun onNetworkStateChanged(enabled: Boolean) {
                if (enabled) {
                    locationTracker?.start()
                    // alert user in notification
                } else {
                    locationTracker?.pause()
                    // alert user in notification
                }
            }
        }
        val intent = Intent()
        intent.action = LocationManager.PROVIDERS_CHANGED_ACTION
        gpsReceiver?.onReceive(this, intent)
        registerReceiver(gpsReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))

        Log.d("Meeeeee", "LocationTrackerService > onCreate end")
    }

    /*override fun onDestroy() {
        super.onDestroy()
        val broadcastIntent = Intent("ac.in.ActivityRecognition.RestartSensor")
        sendBroadcast(broadcastIntent)
    }*/

    private fun getNotification(gpsEnable: Boolean) : Notification {

        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle(getString(R.string.app_name))
        if (gpsEnable)
            bigTextStyle.bigText("سرویس مکان‌یاب خطرنگار در پس‌زمینه در حال اجراست. در صورت بستن برای مکان‌یابی بیشتر باید صبر کنید.")
        else
            bigTextStyle.bigText("جی‌پی‌اس (GPS) دستگاه خاموش است و سرویس قادر به مکان‌یابی نمی‌باشد.")

        val builder = NotificationCompat.Builder(this, channelId)
        builder.priority = NotificationCompat.PRIORITY_MIN
        builder.setCategory(NotificationCompat.CATEGORY_SERVICE)
        builder.setAutoCancel(false)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setStyle(bigTextStyle)
        builder.setContentTitle(getString(R.string.app_name))
        if (gpsEnable)
            builder.setContentText("سرویس مکان‌یاب خطرنگار در پس‌زمینه در حال اجراست...")
        else
            builder.setContentText("جی‌پی‌اس (GPS) دستگاه خاموش است.")

        // Action to stop the service.
        /*val piLaunchMainActivity = getLaunchActivityPI(context)
        val piStopService = getStopServicePI(context)
        val stopAction = NotificationCompat.Action.Builder(
            STOP_ACTION_ICON,
            getNotificationStopActionText(context),
            piStopService
        ).build()*/

        // Add Play button intent in notification.
        /*val playIntent = Intent(this, MyForeGroundService::class.java)
        playIntent.action = ACTION_PLAY
        val pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0)
        val playAction = NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent)
        builder.addAction(playAction)*/

        // Add Pause button intent in notification.
        /*val pauseIntent = Intent(this, MyForeGroundService::class.java)
        pauseIntent.action = ACTION_PAUSE
        val pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0)
        val prevAction = NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPrevIntent)
        builder.addAction(prevAction)*/

        return builder.build()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(): String {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(channelId, channelName, IMPORTANCE_MIN)
        notificationManager.createNotificationChannel(notificationChannel)
        return channelId
    }

    inner class MyLocalBinder : Binder() {
        fun getService() : LocationTrackerService {
            return this@LocationTrackerService
        }
    }

}
