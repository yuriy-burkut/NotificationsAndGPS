package com.yuriy.notificationandgps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LocationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView

    private var latitude = 0.0
    private var longitude = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.location_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            val args: Bundle? = arguments

            if (args != null) {
                latitude = args.getDouble(LATITUDE_KEY)
                longitude = args.getDouble(LONGITUDE_KEY)
            }
        }

        mapView = view.findViewById(R.id.id_map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        map.uiSettings.isMyLocationButtonEnabled = false
        map.isMyLocationEnabled = true
        map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
        map.addMarker(MarkerOptions().position(LatLng(latitude, longitude)))
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
}