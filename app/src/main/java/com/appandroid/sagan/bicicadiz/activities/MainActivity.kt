package com.appandroid.sagan.bicicadiz.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.appandroid.sagan.bicicadiz.ConnectionReceiver
import com.appandroid.sagan.bicicadiz.Constants.APARCABICIS_GEO
import com.appandroid.sagan.bicicadiz.Constants.APARCABICIS_ICON
import com.appandroid.sagan.bicicadiz.Constants.CARRIL_BICI_GEO
import com.appandroid.sagan.bicicadiz.Constants.CARRIL_ID
import com.appandroid.sagan.bicicadiz.Constants.COLOR_BLANCO
import com.appandroid.sagan.bicicadiz.Constants.LAYER_ID
import com.appandroid.sagan.bicicadiz.Constants.PARKING_ID
import com.appandroid.sagan.bicicadiz.Constants.PARKING_LOCATION_NAME
import com.appandroid.sagan.bicicadiz.R
import com.appandroid.sagan.bicicadiz.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.common.location.Location
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.maps.Style.*
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver
import com.mapbox.navigation.core.lifecycle.requireMapboxNavigation
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.ui.maps.NavigationStyles
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import java.io.IOException


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding : ActivityMainBinding
    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private var permissionsManager: PermissionsManager? = null
    private var locationComponent: LocationComponent? = null
    private lateinit var carrilBici: GeoJsonSource
    private lateinit var parkingBicis: GeoJsonSource
    private var br: BroadcastReceiver = ConnectionReceiver()
    private var storage = Firebase.storage
    private lateinit var auth: FirebaseAuth
    private val navigationLocationProvider = NavigationLocationProvider()


    private val locationObserver = object : LocationObserver {
        /**
         * Invoked as soon as the [Location] is available.
         */
        override fun onNewRawLocation(rawLocation: android.location.Location) {
// Not implemented in this example. However, if you want you can also
// use this callback to get location updates, but as the name suggests
// these are raw location updates which are usually noisy.
        }

        /**
         * Provides the best possible location update, snapped to the route or
         * map-matched to the road if possible.
         */
        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            val enhancedLocation = locationMatcherResult.enhancedLocation
            navigationLocationProvider.changePosition(
                enhancedLocation,
                locationMatcherResult.keyPoints,
            )
// Invoke this method to move the camera to your current location.
//            updateCamera(enhancedLocation)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mapView.getMapboxMap().loadStyleUri(NavigationStyles.NAVIGATION_DAY_STYLE)


//        auth = Firebase.auth

//        mapView = binding.mapView
//        mapView!!.onCreate(savedInstanceState)
//        mapView!!.getMapAsync(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        activeReceiver()
    }

    val mapboxNavigation: MapboxNavigation by requireMapboxNavigation(
        onResumedObserver = object : MapboxNavigationObserver {
            @SuppressLint("MissingPermission")
            override fun onAttached(mapboxNavigation: MapboxNavigation) {
                mapboxNavigation.registerLocationObserver(locationObserver)
                mapboxNavigation.startTripSession()
            }

            override fun onDetached(mapboxNavigation: MapboxNavigation) {
                mapboxNavigation.unregisterLocationObserver(locationObserver)
            }
        },
        onInitialize = this::initNavigation
    )

    private fun initNavigation() {
        MapboxNavigationApp.setup(
            NavigationOptions.Builder(this)
                .accessToken(getString(R.string.mapbox_access_token))
                .build()
        )
// Instantiate the location component which is the key component to fetch location updates.
        binding.mapView.location.apply {
            setLocationProvider(navigationLocationProvider)
// Uncomment this block of code if you want to see a circular puck with arrow.
//            locationPuck = LocationPuck2D(
//                bearingImage = ContextCompat.getDrawable(
//                    this@MainActivity,
//                    R.drawable.mapbox_navigation_puck_icon
//                )
//            )
// When true, the blue circular puck is shown on the map. If set to false, user
// location in the form of puck will not be shown on the map.
            enabled = true
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap

        loadMap(TRAFFIC_NIGHT)
        mapboxMap.setMaxZoomPreference(18.0)
        mapboxMap.setMinZoomPreference(12.0)

        binding.zoomTolayer.setOnClickListener {
             val position = CameraPosition.Builder()
                    .target(LatLng(36.514444, -6.279378))
                    .zoom(12.0)
                    .tilt(0.0)
                    .bearing(0.0)
                    .build()
            mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 5000)
        }

        binding.zoomTolocation.setOnClickListener{
           locationComponent!!.cameraMode = CameraMode.TRACKING
        }

        mapboxMap.addOnMapClickListener { point ->
            val screenPoint = mapboxMap.projection.toScreenLocation(point)
            val features = mapboxMap.queryRenderedFeatures(screenPoint, LAYER_ID)
            if (features.isNotEmpty()) {
                val selectedFeature = features[0]
                val title = selectedFeature.getStringProperty(PARKING_LOCATION_NAME)

                if(title.isNullOrEmpty()){
                    Toast.makeText(this@MainActivity, getString(R.string.estacionamiento_sin_nombre), Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this@MainActivity, title, Toast.LENGTH_SHORT).show()
                    }
            }
            false
        }
    }

    override fun onBackPressed() {}

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu.setGroupDividerEnabled(true)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.streets) {
            loadMap(MAPBOX_STREETS)

            return true
        }
        if (id == R.id.satellite_streets) {
            loadMap(SATELLITE_STREETS)
            return true
        }
        if (id == R.id.traffic) {
            loadMap(TRAFFIC_NIGHT)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

//    override fun onExplanationNeeded(permissionsToExplain: List<String>) {}
//
//    override fun onPermissionResult(granted: Boolean) {
//        if (granted) {
//            mapboxMap!!.getStyle { style -> enableLocationComponent(style) }
//        } else {
//            Toast.makeText(this@MainActivity, getString(R.string.no_ubicacion), Toast.LENGTH_LONG).show()
//            finish()
//        }
//    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }

    private fun loadMap(base: String) {
        mapboxMap!!.setStyle(base
        ) { style ->
//            enableLocationComponent(style)
            loadCarriles(style)
            switchAparcaBicis(style)
            if(binding.swAparcabicis.isChecked){
                 loadAparcaBicis(style)
            }
         }
    }
//
//    private fun enableLocationComponent(style: Style) {
//        if (PermissionsManager.areLocationPermissionsGranted(this@MainActivity)) {
//            val locationComponentOptions = LocationComponentOptions.builder(this)
//                .pulseEnabled(true)
//                .pulseColor(Color.argb(255,159, 237, 254))
//                .pulseAlpha(.100f)
//                .pulseInterpolator(BounceInterpolator())
//                .build()
//
//            val locationComponentActivationOptions = LocationComponentActivationOptions
//                .builder(this, style)
//                .locationComponentOptions(locationComponentOptions)
//                .build()
//
//            locationComponent = mapboxMap!!.locationComponent
//            locationComponent!!.activateLocationComponent(locationComponentActivationOptions)
//
//            if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//                return
//            }
//            locationComponent!!.isLocationComponentEnabled = true
//            locationComponent!!.renderMode = RenderMode.COMPASS
//
//        } else {
//            permissionsManager = PermissionsManager(this@MainActivity)
//            permissionsManager!!.requestLocationPermissions(this@MainActivity)
//        }
//    }

    override fun onStart() {
        super.onStart()
//        mapView!!.onStart()
    }

    override fun onResume() {
        super.onResume()
        activeReceiver()
//        mapView!!.onResume()
     }

    override fun onPause() {
        super.onPause()
//        mapView!!.onPause()
    }

    override fun onStop() {
        super.onStop()
//        mapView!!.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
//        mapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
//        mapView!!.onLowMemory()
    }

    private fun loadJsonFromAsset(filename: String): String? {
        return try {
            val `is` = assets.open(filename)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()

            String(buffer)

        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    private fun loadCarriles(style: Style){

        carrilBici = GeoJsonSource(CARRIL_ID, loadJsonFromAsset(CARRIL_BICI_GEO))

        style.addSource(carrilBici)
        style.addLayer(LineLayer("carril_background_layer", CARRIL_ID)
            .withProperties(
                lineCap(Property.LINE_CAP_SQUARE),
                lineJoin(Property.LINE_JOIN_MITER),
                lineOpacity(.7f),
                lineWidth(8f),
                lineColor(Color.parseColor(COLOR_BLANCO))
            ))

        style.addLayer(LineLayer("carril_layer", CARRIL_ID)
            .withProperties(
                lineCap(Property.LINE_CAP_SQUARE),
                lineJoin(Property.LINE_JOIN_MITER),
                lineOpacity(.7f),
                lineWidth(4f),
                lineColor(Color.parseColor("#329221"))
            ))
    }

    private fun loadAparcaBicis(style: Style){
        parkingBicis = GeoJsonSource(PARKING_ID, loadJsonFromAsset(APARCABICIS_GEO))
        style.addSource(parkingBicis)
        style.addImage(APARCABICIS_ICON, BitmapFactory.decodeResource(this.resources,
            R.drawable.mapbox_marker_icon_default
        ))
        val symbolLayer = SymbolLayer(LAYER_ID, PARKING_ID)
        symbolLayer.withProperties(iconImage(APARCABICIS_ICON), iconAllowOverlap(true), iconSize(0.9f))
        style.addLayer(symbolLayer)
    }

    private fun switchAparcaBicis(style: Style){
        binding.swAparcabicis.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {
                loadAparcaBicis(style)
                } else {
                    if(style.layers.isNotEmpty()){
                        style.removeLayer(LAYER_ID)
                        style.removeSource(PARKING_ID)
                    }
               }
        }
    }

    private fun activeReceiver(){
        val networkIntentFilter = IntentFilter()
        networkIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(br, networkIntentFilter)
    }

}


