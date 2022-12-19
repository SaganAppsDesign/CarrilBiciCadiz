package com.appandroid.sagan.bicicadiz.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.appandroid.sagan.bicicadiz.ConnectionReceiver
import com.appandroid.sagan.bicicadiz.Constants.APARCABICIS_GEO
import com.appandroid.sagan.bicicadiz.Constants.APARCA_BICIS
import com.appandroid.sagan.bicicadiz.Constants.CARRIL_BICI_GEO
import com.appandroid.sagan.bicicadiz.Constants.CARRIL_ID
import com.appandroid.sagan.bicicadiz.Constants.LAYER_ID
import com.appandroid.sagan.bicicadiz.Constants.PARKING_ID
import com.appandroid.sagan.bicicadiz.Constants.PARKING_LOCATION_NAME
import com.appandroid.sagan.bicicadiz.R
import com.appandroid.sagan.bicicadiz.databinding.ActivityMainBinding
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.common.location.Location
import com.mapbox.geojson.Point.fromLngLat
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style.Companion.MAPBOX_STREETS
import com.mapbox.maps.Style.Companion.OUTDOORS
import com.mapbox.maps.Style.Companion.SATELLITE
import com.mapbox.maps.Style.Companion.SATELLITE_STREETS
import com.mapbox.maps.Style.Companion.TRAFFIC_NIGHT
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.expressions.dsl.generated.zoom
import com.mapbox.maps.extension.style.image.image
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.animation.CameraAnimatorOptions.Companion.cameraAnimatorOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver
import com.mapbox.navigation.core.lifecycle.requireMapboxNavigation
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.ui.maps.internal.ui.LocationComponent
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import java.security.Permissions


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var mapView: MapView? = null
    private var mapboxMap: com.mapbox.maps.MapboxMap? = null
    private var br: BroadcastReceiver = ConnectionReceiver()
    private lateinit var locationPermissionHelper: LocationPermissionHelper

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView?.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView?.getMapboxMap()?.setCamera(CameraOptions.Builder().center(it).build())
        mapView?.gestures?.focalPoint = mapView?.getMapboxMap()?.pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
//            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = binding.mapView
        loadMap(TRAFFIC_NIGHT)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        activeReceiver()

        binding.zoomTolayer.setOnClickListener {
            val cameraPosition = CameraOptions.Builder()
                .center(fromLngLat(-6.279378, 36.514444))
                .pitch(45.0)
                .zoom(12.0)
                .bearing(-17.6)
                .build()
            mapView!!.getMapboxMap().setCamera(cameraPosition)

            mapboxMap?.flyTo(
                cameraOptions {
                    zoom(12.0) // Sets the zoom
                    bearing(180.0) // Rotate the camera
                    pitch(50.0) // Set the camera pitch
                },
                mapAnimationOptions {
                    duration(7000)
                }
            )
        }

//        mapboxMap?.addOnMapClickListener { point ->
////            val screenPoint = mapboxMap.projection.toScreenLocation(point)
//            val screenPoint = mapboxMap!!.projectedMetersForCoordinate(point)
//            val features = mapboxMap.queryRenderedFeatures()
//            if (features.isNotEmpty()) {
//                val selectedFeature = features[0]
//                val title = selectedFeature.getStringProperty(PARKING_LOCATION_NAME)
//
//                if(title.isNullOrEmpty()) Toast.makeText(this, getString(R.string.estacionamiento_sin_nombre), Toast.LENGTH_SHORT).show()
//                else Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
//            }
//            false
//        }


//        binding.zoomTolocation.setOnClickListener{
//            locationComponent!!.cameraMode = CameraMode.TRACKING
//        }


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
        if (id == R.id.outdoors) {
            loadMap(OUTDOORS)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }

//    private fun loadMap(base: String) {
//        mapboxMap!!.setStyle(base
//        ) { style ->
//            enableLocationComponent(style)
//            loadCarriles(style)
//            switchAparcaBicis(style)
//            if(binding.swAparcabicis.isChecked){
//                 loadAparcaBicis(style)
//            }
//         }
//    }


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



    override fun onResume() {
        super.onResume()
        activeReceiver()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    private fun loadMap(base: String) {
        loadCarriles(base)
        switchAparcaBicis(base)
        if(binding.swAparcabicis.isChecked){
            loadCarriles(base)
        }
    }

    private fun loadCarriles(base: String){
        mapView?.getMapboxMap()?.loadStyle(style (styleUri = base) {
            //enableLocationComponent(style)
           +geoJsonSource(id = CARRIL_ID) {
                url("asset://$CARRIL_BICI_GEO")}

            +lineLayer("carril_background_layer", CARRIL_ID) {
                lineColor(Color.parseColor("#ffffff"))
                lineWidth(8.0)
                lineCap()
                lineJoin()
            }
            +lineLayer("carril_layer", CARRIL_ID) {
                lineColor(Color.parseColor("#329221"))
                lineWidth(4.0)
                lineCap(LineCap.ROUND)
                lineJoin(LineJoin.ROUND)
            }

            +image(APARCA_BICIS) {
                bitmap(BitmapFactory.decodeResource(resources, R.drawable.mapbox_marker_icon_default))
            }
            +geoJsonSource(id = PARKING_ID) {
                url("asset://$APARCABICIS_GEO")}

            +symbolLayer(LAYER_ID, PARKING_ID) {
                sourceLayer(PARKING_ID)
                iconImage(APARCA_BICIS)
                iconAllowOverlap(true)
                iconSize(0.9)
            }
        })
    }


    private fun removeAparcabicis() {
       mapView?.getMapboxMap()?.getStyle { style ->
        if (style.styleLayerExists(LAYER_ID)) {
            style.removeStyleLayer(LAYER_ID)
        }
        }
    }


    private fun switchAparcaBicis(base: String){
        binding.swAparcabicis.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked) {
                loadCarriles(base)
                } else {
                removeAparcabicis()
            }
        }
    }

    private fun activeReceiver(){
        val networkIntentFilter = IntentFilter()
        networkIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(br, networkIntentFilter)
    }

    private fun animateCameraDelayed() {
        binding.mapView.camera.apply {
            val bearing = createBearingAnimator(cameraAnimatorOptions(-45.0)) {
                duration = 4000
                interpolator = AccelerateDecelerateInterpolator()
            }
            val zoom = createZoomAnimator(
                cameraAnimatorOptions(14.0) {
                    startValue(3.0)
                }
            ) {
                duration = 4000
                interpolator = AccelerateDecelerateInterpolator()
            }
            val pitch = createPitchAnimator(
                cameraAnimatorOptions(55.0) {
                    startValue(0.0)
                }
            ) {
                duration = 4000
                interpolator = AccelerateDecelerateInterpolator()
            }

            playAnimatorsSequentially(zoom, pitch, bearing)
        }
    }
}


