package com.appandroid.sagan.bicicadiz

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
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
    // private Button startButton;
    // private Button stopButton;
    // variables for calculating and drawing a route
    //private DirectionsRoute currentRoute;
    // private static final String TAG = "DirectionsActivity";
    //private NavigationMapRoute navigationMapRoute;
    //private Switch tramos_totales, tramos_obras;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       this.title = "      - Carril Bici Cádiz -"

        Mapbox.getInstance(this, "pk.eyJ1IjoiZGFyZW5hcyIsImEiOiJjanc2ZWhzNmYwMXJ1NGJuamRiZzhteDRiIn0.cfBhxA6KOQJvqqfpkDrT0A")

        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)

        //stopButton = findViewById ( R.id.stopButton );
        //startButton = findViewById ( R.id.startButton );

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

        /*    for (int i=0; i < 2; i++) {

            Toast toast = Toast.makeText ( MainActivity.this, "Toca un punto en el mapa para empezar la navegación desde tu ubicación actual", Toast.LENGTH_LONG );

            toast.setGravity ( Gravity.CENTER, 0, 0 );

            toast.show ();

        }*/


        val zoomTolayer = findViewById<FloatingActionButton>(R.id.zoomTolayer)

        this.mapboxMap = mapboxMap

        cargarMapa(TRAFFIC_NIGHT)

        //Zoom to layer
        zoomTolayer.setOnClickListener { view ->

            Toast.makeText(this@MainActivity, "Zoom a la capa", Toast.LENGTH_LONG).show()

            val position = CameraPosition.Builder()
                    .target(LatLng(36.514444, -6.279378))
                    .zoom(11.0)
                    .tilt(25.0)
                    .bearing(0.0)
                    .build()

            mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 5000)

        }


        /*  stopButton.setOnClickListener ( view ->


        {
            cargarMapa ( TRAFFIC_NIGHT );
            navigationMapRoute.removeRoute();


            stopButton.setEnabled(false);
            stopButton.setBackgroundColor (Color.argb (255,215,215,215));
            startButton.setEnabled(false);
            startButton.setBackgroundColor (Color.argb (255,215,215,215));

        }


        );*/


        //Info markers parking bicis

        mapboxMap.addOnMapClickListener { point ->
            val screenPoint = mapboxMap.projection.toScreenLocation(point)

            val features = mapboxMap.queryRenderedFeatures(screenPoint, "layer-id")
            if (!features.isEmpty()) {
                val selectedFeature = features[0]
                val title = selectedFeature.getStringProperty("localizacion")
                Toast.makeText(this@MainActivity, "Parking bici $title", Toast.LENGTH_SHORT).show()
            }

            false

        } //cierre


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


    fun cargarMapa(base: String) {


        mapboxMap!!.setStyle(base


        ) { style ->

            enableLocationComponent(style)

            //  addDestinationIconSymbolLayer(style);

            //   mapboxMap.addOnMapClickListener(MainActivity.this);


            //Botón Navigation

            /* startButton.setOnClickListener ( view -> {


        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                .directionsRoute ( currentRoute )
                .build ();

        NavigationLauncher.startNavigation (MainActivity.this, options);




    } );*/


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

        }//cierre estilo

    }


    /*  private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {

            loadedMapStyle.addImage("destination-icon-id", BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
            GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
            loadedMapStyle.addSource(geoJsonSource);

            SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");

            destinationSymbolLayer.withProperties(
              iconImage("destination-icon-id"),
              iconAllowOverlap(true),
              iconIgnorePlacement(true)
            );
            loadedMapStyle.addLayer(destinationSymbolLayer);
          }*/

    /*
            @SuppressWarnings( {"MissingPermission"})
            @Override
            public boolean onMapClick(@NonNull LatLng point) {

                Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                        locationComponent.getLastKnownLocation().getLatitude());

                GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
                if (source != null) {
                    source.setGeoJson ( Feature.fromGeometry ( destinationPoint ) );
                }

                getRoute(originPoint, destinationPoint);

                startButton.setEnabled(true);
                stopButton.setEnabled(true);
                startButton.setBackgroundResource(R.color.design_default_color_primary_dark);
                stopButton.setBackgroundResource(R.color.mapbox_navigation_route_layer_congestion_red);
                return true;

                }


                private void getRoute(Point origin, Point destination) {
                    NavigationRoute.builder(this)
                            .accessToken(Mapbox.getAccessToken())
                            .origin(origin)
                            .destination(destination)
                            .build()
                            .getRoute(new Callback<DirectionsResponse> () {


                                @Override
                                public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                                    // You can get the generic HTTP info about the response
                                    Log.d(TAG, "Response code: " + response.code());
                                    if (response.body() == null) {
                                        Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                                        return;
                                    } else if (response.body().routes().size() < 1) {
                                        Log.e(TAG, "No routes found");
                                        return;
                                    }

                                    currentRoute = response.body().routes().get(0);

                                // Draw the route on the map
                                    if (navigationMapRoute != null) {
                                        navigationMapRoute.removeRoute();
                                    } else {
                                        navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                                    }
                                    navigationMapRoute.addRoute(currentRoute);
                                }

                                @Override
                                public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                                    Log.e(TAG, "Error: " + t.getMessage());

                                }
                            });
                }


*/

    //Ubicación actual

    private fun enableLocationComponent(estilo: Style) {


        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this@MainActivity)) {

            // Get an instance of the component
            locationComponent = mapboxMap!!.locationComponent

            // Activate with options
            locationComponent!!.activateLocationComponent(LocationComponentActivationOptions.builder(this@MainActivity, estilo).build())

            // Enable to make component visible
            if (ActivityCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return
            }

            locationComponent!!.isLocationComponentEnabled = true

            // Set the component's camera mode
            // locationComponent.setCameraMode(CameraMode.TRACKING);

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


