package ir.am3n.khatarnegar

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionRequest {

    companion object {


        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        const val SMS_PERMISSION_REQUEST_CODE = 9900
        const val RQST_CALLPHONE = 1006


        fun checkIfAlreadyhaveLocationPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun requestForLocationPermission(context: Activity) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PermissionRequest.LOCATION_PERMISSION_REQUEST_CODE
            )
        }



        fun checkIfAlreadyhaveSMSPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECEIVE_SMS
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun requestForSMSPermission(context: Activity) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                PermissionRequest.SMS_PERMISSION_REQUEST_CODE
            )
        }

        fun requestForSMSPermission(context: Activity, request_code: Int) {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.RECEIVE_SMS), request_code)
        }



        fun haveCallPhone(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun requestForCallPhone(context: Activity) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.CALL_PHONE),
                PermissionRequest.RQST_CALLPHONE
            )
        }


    }

}
