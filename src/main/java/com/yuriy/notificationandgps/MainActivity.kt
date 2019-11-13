package com.yuriy.notificationandgps

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

const val MY_PERMISSIONS_REQUEST_LOCATION = 1
const val OPEN_MAP_REQUEST_CODE = 11
const val CHANNEL_ID = "GpsNotification"
const val LATITUDE_KEY = "latitude"
const val LONGITUDE_KEY = "longitude"

class MainActivity : AppCompatActivity(), MainFragment.OnButtonsClickListener {

    var mainFragment: MainFragment = MainFragment().apply {
        setOnButtonsClickListener(this@MainActivity)
    }
    var locationFragment: LocationFragment = LocationFragment()

    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private lateinit var locationManger: LocationManager

    private val locationListener
        get() = object : LocationListener {
            override fun onLocationChanged(p0: Location?) = p0?.let { updateLocation(it) } ?: Unit
            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) = Unit
            override fun onProviderEnabled(p0: String?) = Unit
            override fun onProviderDisabled(p0: String?) = Unit
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.hasExtra(LATITUDE_KEY) && intent.hasExtra(LONGITUDE_KEY)) {
            longitude = intent.extras?.getDouble(LONGITUDE_KEY) ?: 0.0
            latitude = intent.extras?.getDouble(LATITUDE_KEY) ?: 0.0

            val args = Bundle()
            args.putDouble(LONGITUDE_KEY, longitude)
            args.putDouble(LATITUDE_KEY, latitude)
            locationFragment.arguments = args

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, locationFragment).commit()
        } else {
            if (savedInstanceState == null) {
                with(supportFragmentManager) {
                    beginTransaction().add(R.id.fragment_container, mainFragment).commit()
                }
            }
        }

        locationManger = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }


    override fun onLocationButtonClick() {

        if (checkPermission()) {
            locationManger.apply {
                requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    10f,
                    locationListener
                )
                requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    10f,
                    locationListener
                )
            }
            openMap()
        }
    }

    override fun onNotificationButtonClick() {
        if (checkPermission()) {
            locationManger.apply {
                requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000 * 10,
                    10f,
                    locationListener
                )
                requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000 * 10,
                    10f,
                    locationListener
                )
            }
            showNotification()
        }
    }

    private fun checkPermission(): Boolean {

        var result = false
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                    .setMessage(getString(R.string.permission_message))
                    .setTitle(getString(R.string.permission_message_title))
                    .create()
                    .show()
                result = false
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        else {
            result = true
        }
        return result
    }

    private fun openMap() {
        val args = Bundle()
        args.putDouble(LONGITUDE_KEY, longitude)
        args.putDouble(LATITUDE_KEY, latitude)
        locationFragment.arguments = args

        with(supportFragmentManager) {
            beginTransaction()
                .replace(R.id.fragment_container, locationFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val openMapIntent = Intent(this, MainActivity::class.java).apply {
            putExtra(LATITUDE_KEY, latitude)
            putExtra(LONGITUDE_KEY, longitude)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, OPEN_MAP_REQUEST_CODE,
            openMapIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        createNotificationChannel()

        val notificationBuilder = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Location")
            .setContentText("Latitude: $latitude Longitude: $longitude")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).apply {
            notify(taskId, notificationBuilder.build())
        }
    }


    private fun updateLocation(location: Location?) {
        if (location == null) {
            return
        }

        if (location.provider.equals(LocationManager.GPS_PROVIDER)) {
            latitude = location.latitude
            longitude = location.longitude
        }
    }
}
