package com.appandroid.sagan.bicicadiz

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.maps.Style.*
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import java.io.IOException


class MainActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener, NavigationView.OnNavigationItemSelectedListener {


    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private var permissionsManager: PermissionsManager? = null
    private var locationComponent: LocationComponent? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, R.string.mapbox_access_token.toString())

        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.itemIconTintList = null
    }

    override fun onMapReady(mapboxMap: MapboxMap) {

        val zoomTolayer = findViewById<FloatingActionButton>(R.id.zoomTolayer)
        val zoomTolocation = findViewById<FloatingActionButton>(R.id.zoomTolocation)

        this.mapboxMap = mapboxMap

        cargarMapa(TRAFFIC_NIGHT)

        mapboxMap.setMaxZoomPreference(17.0)
        mapboxMap.setMinZoomPreference(11.0)

        //Zoom to layer
        zoomTolayer.setOnClickListener {

            Toast.makeText(this@MainActivity, "Zoom a la capa", Toast.LENGTH_LONG).show()
            val position = CameraPosition.Builder()

                    .target(LatLng(36.514444, -6.279378))
                    .zoom(12.0)
                    .tilt(0.0)
                    .bearing(0.0)
                    .build()

            //mapboxMap.setMinZoomPreference(12.0)
            mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 5000)
        }

        zoomTolocation.setOnClickListener{
            Toast.makeText(this@MainActivity, "Zoom a la ubicación actual", Toast.LENGTH_LONG).show()
            // Set the component's camera mode
            locationComponent!!.cameraMode = CameraMode.TRACKING_GPS_NORTH

        }
        //Info markers parking bicis

        mapboxMap.addOnMapClickListener { point ->
            val screenPoint = mapboxMap.projection.toScreenLocation(point)

            val features = mapboxMap.queryRenderedFeatures(screenPoint, "layer-id")
            if (features.isNotEmpty()) {
                val selectedFeature = features[0]
                val title = selectedFeature.getStringProperty("localizacion")

                if(title.isNullOrEmpty()){

                    Toast.makeText(this@MainActivity, "Estacionamiento bici", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this@MainActivity, "Estacionamiento $title", Toast.LENGTH_SHORT).show()
                }
            }
            false
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.streets) {
            cargarMapa(MAPBOX_STREETS)
            return true
        }
        if (id == R.id.satellite_streets) {
            cargarMapa(SATELLITE_STREETS)
            return true
        }
        if (id == R.id.traffic) {
            cargarMapa(TRAFFIC_NIGHT)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.switch_tramos_totales) {

       } else if (id == R.id.switch_parkings_actuales) {

        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {

    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapboxMap!!.getStyle { style -> enableLocationComponent(style) }
        } else {
            Toast.makeText(this@MainActivity, "No encuentra ubicación. Espere un momento...", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun loadJsonFromAsset(filename: String): String? {
        // Using this method to load in GeoJSON files from the assets folder.
        try {
            val `is` = assets.open(filename)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()

            return String(buffer)

        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }

    private fun cargarMapa(base: String) {
        mapboxMap!!.setStyle(base
        ) { style ->
                enableLocationComponent(style)
            // Add line Tramos totales importing geojson

            val carrilBici1 = GeoJsonSource("carril_id", loadJsonFromAsset("tramos_totales.geojson"))

            style.addSource(carrilBici1)

            style.addLayer(LineLayer("linelayer1", "carril_id")
                    .withProperties(PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                            PropertyFactory.lineOpacity(.7f),
                            PropertyFactory.lineWidth(6f),
                            PropertyFactory.lineColor(Color.parseColor("#FFFFFF"))))

            style.addLayer(LineLayer("linelayer", "carril_id")
                    .withProperties(PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                            PropertyFactory.lineOpacity(.7f),
                            PropertyFactory.lineWidth(4f),
                            PropertyFactory.lineColor(Color.parseColor("#329221"))))


            val tramoInterurbano = GeoJsonSource("carril_id2", loadJsonFromAsset("tramo_interurbano.geojson"))

            style.addSource(tramoInterurbano)

            style.addLayer(LineLayer("linelayer2", "carril_id2")
                    .withProperties(PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                            PropertyFactory.lineOpacity(.7f),
                            PropertyFactory.lineWidth(6f),
                            PropertyFactory.lineColor(Color.parseColor("#FFFFFF"))))

            style.addLayer(LineLayer("linelayer3", "carril_id2")
                    .withProperties(PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                            PropertyFactory.lineOpacity(.7f),
                            PropertyFactory.lineWidth(4f),
                            PropertyFactory.lineColor(Color.parseColor("#0b52d6"))))
            // Add markers parking bicis importing geojson

            val parkingBicis = GeoJsonSource("parking_id", loadJsonFromAsset("parking_bicis.geojson"))
            style.addSource(parkingBicis)
            style.addImage("parking-bicis", BitmapFactory.decodeResource(this.resources, R.drawable.icono_bici_markers))
            val symbolLayer = SymbolLayer("layer-id", "parking_id")
            symbolLayer.withProperties(iconImage("parking-bicis"), iconAllowOverlap(true)
            )
            style.addLayer(symbolLayer)
        }
    }

    private fun enableLocationComponent(estilo: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(this@MainActivity)) {
            locationComponent = mapboxMap!!.locationComponent
            locationComponent!!.activateLocationComponent(LocationComponentActivationOptions.builder(this@MainActivity, estilo).build())
            if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            locationComponent!!.isLocationComponentEnabled = true
            // Set the component's camera mode
            //locationComponent!!.cameraMode = CameraMode.TRACKING
            // Set the component's render mode
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
}


