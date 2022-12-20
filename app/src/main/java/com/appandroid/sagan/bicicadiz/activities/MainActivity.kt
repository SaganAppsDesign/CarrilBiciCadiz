package com.appandroid.sagan.bicicadiz.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.BounceInterpolator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.appandroid.sagan.bicicadiz.ConnectionReceiver
import com.appandroid.sagan.bicicadiz.Constants.APARCABICIS_GEO
import com.appandroid.sagan.bicicadiz.Constants.APARCABICIS_ICON
import com.appandroid.sagan.bicicadiz.Constants.CARRIL_BICI_GEO
import com.appandroid.sagan.bicicadiz.Constants.CARRIL_ID
import com.appandroid.sagan.bicicadiz.Constants.COLOR_BLANCO
import com.appandroid.sagan.bicicadiz.Constants.FUENTES_GEO
import com.appandroid.sagan.bicicadiz.Constants.FUENTES_ICON
import com.appandroid.sagan.bicicadiz.Constants.FUENTES_ID
import com.appandroid.sagan.bicicadiz.Constants.LAYER_FUENTES_ID
import com.appandroid.sagan.bicicadiz.Constants.LAYER_ID
import com.appandroid.sagan.bicicadiz.Constants.PARKING_ID
import com.appandroid.sagan.bicicadiz.Constants.PARKING_LOCATION_NAME
import com.appandroid.sagan.bicicadiz.R
import com.appandroid.sagan.bicicadiz.databinding.ActivityMainBinding
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.matching.v5.MapboxMapMatching
import com.mapbox.api.matching.v5.models.MapMatchingResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
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
import com.mapbox.maps.extension.observable.model.Response
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.pluginscalebar.ScaleBarOptions
import com.mapbox.pluginscalebar.ScaleBarPlugin
import okhttp3.Call
import okhttp3.Callback
import java.io.IOException


class MainActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {

    private lateinit var binding : ActivityMainBinding
    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private var permissionsManager: PermissionsManager? = null
    private var locationComponent: LocationComponent? = null
    private lateinit var carrilBici: GeoJsonSource
    private lateinit var parkingBicis: GeoJsonSource
    private lateinit var fuentes: GeoJsonSource
    private var br: BroadcastReceiver = ConnectionReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = binding.mapView
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        activeReceiver()
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
                    Toast.makeText(this, getString(R.string.estacionamiento_sin_nombre), Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
                    }
            }
            false
        }

        scaleBar(mapView, mapboxMap)
    }

    override fun onBackPressed() {}

    @RequiresApi(Build.VERSION_CODES.P)
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
        if (id == R.id.light) {
            loadCustomMap()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {}

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapboxMap!!.getStyle { style -> enableLocationComponent(style) }
        } else {
            Toast.makeText(this@MainActivity, getString(R.string.no_ubicacion), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun loadMap(base: String) {
        mapboxMap!!.setStyle(base
        ) { style ->
            enableLocationComponent(style)
            loadCarriles(style)
            switchAparcaBicis(style)
            switchFuentes(style)
            if(binding.swAparcabicis.isChecked){
                 loadAparcaBicis(style)
            }
            if(binding.swFuentes.isChecked){
                loadFuentes(style)
            }
         }
    }

    private fun loadCustomMap() {
        mapboxMap!!.setStyle(Builder().fromUri("mapbox://styles/darenas/ck0xul2h401cx1cmgkpovc27m")) {
                style ->
            enableLocationComponent(style)
            loadCarriles(style)
            switchAparcaBicis(style)
            switchFuentes(style)
            if(binding.swAparcabicis.isChecked){
                loadAparcaBicis(style)
            }
            if(binding.swFuentes.isChecked){
                loadFuentes(style)
            }
        }
    }

    private fun enableLocationComponent(style: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(this@MainActivity)) {
            val locationComponentOptions = LocationComponentOptions.builder(this)
                .pulseEnabled(true)
                .pulseColor(Color.argb(255,159, 237, 254))
                .pulseAlpha(.100f)
                .pulseInterpolator(BounceInterpolator())
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions
                .builder(this, style)
                .locationComponentOptions(locationComponentOptions)
                .build()

            locationComponent = mapboxMap!!.locationComponent
            locationComponent!!.activateLocationComponent(locationComponentActivationOptions)

            if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                return
            }
            locationComponent!!.isLocationComponentEnabled = true
            locationComponent!!.renderMode = RenderMode.COMPASS

        } else {
            permissionsManager = PermissionsManager(this@MainActivity)
            permissionsManager!!.requestLocationPermissions(this@MainActivity)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onResume() {
        super.onResume()
        activeReceiver()
        mapView!!.onResume()
     }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
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
            R.drawable.parking_bici
        ))
        val symbolLayer = SymbolLayer(LAYER_ID, PARKING_ID)
        symbolLayer.withProperties(iconImage(APARCABICIS_ICON), iconAllowOverlap(false), iconSize(0.3f), iconIgnorePlacement(false))
        style.addLayer(symbolLayer)
    }

    private fun loadFuentes(style: Style){
        fuentes = GeoJsonSource(FUENTES_ID, loadJsonFromAsset(FUENTES_GEO))
        style.addSource(fuentes)
        style.addImage(FUENTES_ICON, BitmapFactory.decodeResource(this.resources,
            R.drawable.fuente
        ))
        val symbolLayer = SymbolLayer(LAYER_FUENTES_ID, FUENTES_ID)
        symbolLayer.withProperties(iconImage(FUENTES_ICON), iconAllowOverlap(false), iconSize(0.15f), iconIgnorePlacement(false))
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

    private fun switchFuentes(style: Style){
        binding.swFuentes.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {
                loadFuentes(style)
            } else {
                if(style.layers.isNotEmpty()){
                    style.removeLayer(LAYER_FUENTES_ID)
                    style.removeSource(FUENTES_ID)
                }
            }
        }
    }

    private fun activeReceiver(){
        val networkIntentFilter = IntentFilter()
        networkIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(br, networkIntentFilter)
    }

    private fun scaleBar(mapView: MapView?, mapBoxMap: MapboxMap){
        val scaleBarPlugin = ScaleBarPlugin(mapView!!, mapBoxMap)
        val scaleBarOptions = ScaleBarOptions(this)
        scaleBarOptions
            .setTextColor(R.color.colorAccent)
            .setTextSize(30f)
            .setBarHeight(15f)
            .setBorderWidth(5f)
            .setMetricUnit(true)
            .setRefreshInterval(15)
            .setMarginTop(30f)
            .setMarginLeft(20f)
            .setTextBarMargin(15f)

        scaleBarPlugin.create(scaleBarOptions)
    }
//
//    private fun customNavigaation(){
//        val singlePoint = Point.fromLngLat(45.55,56.55)
//        val singlePoint2 = Point.fromLngLat(43.55,53.55)
//        val customRoutePointList = listOf(singlePoint, singlePoint2)
//        val mapboxMapMatchingRequest = MapboxMapMatching.builder()
//            .accessToken(getString(R.string.mapbox_access_token))
//            .coordinates(customRoutePointList)
//            .steps(true)
//            .voiceInstructions(true)
//            .bannerInstructions(true)
//            .profile(DirectionsCriteria.PROFILE_DRIVING)
//            .build()
//
//        mapboxMapMatchingRequest.enqueueCall(object : Callback<MapMatchingResponse> {
//            override fun onResponse(call: Call<MapMatchingResponse>, response: Response<MapMatchingResponse>) {
//                if (response.isSuccessful) {
//                    response.body()?.matchings()?.let { matchingList ->
//                        matchingList[0].toDirectionRoute().toNavigationRoute(
//                            RouterOrigin.Custom()
//                        ).apply {
//                            mapboxNavigation?.setNavigationRoutes(listOf(this))
//                        }
//                    }
//
//                }
//            }
//
//            override fun onFailure(call: Call<MapMatchingResponse>, throwable: Throwable) {
//
//            }
//        })
//    }
}


