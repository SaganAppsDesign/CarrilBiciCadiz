package com.appandroid.sagan.bicicadiz

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.appandroid.sagan.bicicadiz.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPointStyle

object Functions: AppCompatActivity() {

    fun ImageView.loadUrl(url: String? = null, radius: Int) {
        Glide.with(context)
            .load(url)
            .fitCenter()
            .transform(RoundedCorners(radius))
            .into(this)
    }

    fun loadAd(binding: ActivityMainBinding){
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        binding.adView.adListener = object: AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                Log.e("Load Add Error", "$adError")
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }
    }

    fun initMap(mMap: GoogleMap, googleMap: GoogleMap, binding: ActivityMainBinding, context: Context){

        val cadiz = LatLng(36.517676, -6.276978)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cadiz))

        googleMap.apply {
            mapType = GoogleMap.MAP_TYPE_HYBRID
            moveCamera(CameraUpdateFactory.zoomTo(13F))
            isTrafficEnabled = true
            mMap.setPadding(0,150,0,150)
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled = true
        }
        loadCarrilBici(mMap, context)
        loadAparcaBicis(mMap, context, binding)
        loadFuentes(mMap, context, binding)
    }

    fun loadCarrilBici(mMap: GoogleMap, context: Context){
        val carrilBici = GeoJsonLayer(mMap, R.raw.carril_bici, context)
        val styleCarril = carrilBici.defaultLineStringStyle
        styleCarril.color = Color.parseColor("#F1C40F")
        styleCarril.width = 15F
        carrilBici.addLayerToMap()
    }

    fun loadAparcaBicis(mMap: GoogleMap, context: Context, binding: ActivityMainBinding){
        val aparcabicis = GeoJsonLayer(mMap, R.raw.aparcabicis, context)

        for (feature in aparcabicis.features) {
            if (feature.getProperty("name") != null) {
                val name = feature.getProperty("name")
                val pointIcon = BitmapDescriptorFactory.fromResource(R.drawable.bicicleta)
                val pointStyle = GeoJsonPointStyle()
                pointStyle.icon = pointIcon
                pointStyle.title = name
                feature.pointStyle = pointStyle
            }
            binding.swAparcabicis.setOnCheckedChangeListener{_, isChecked ->
                if (isChecked) {
                    aparcabicis.addLayerToMap()
                } else {
                    aparcabicis.removeLayerFromMap()
                }
            }
        }
    }

    fun loadFuentes(mMap: GoogleMap, context: Context, binding: ActivityMainBinding){
        val fuentes = GeoJsonLayer(mMap, R.raw.fuentes, context)
        for (feature in fuentes.features) {
            if (feature.getProperty("type") != null) {
                val pointIcon = BitmapDescriptorFactory.fromResource(R.drawable.fuente)
                val pointStyle = GeoJsonPointStyle()
                pointStyle.icon = pointIcon
                pointStyle.title = "Fuente de agua potable"
                feature.pointStyle = pointStyle
            }
        }
        binding.swFuentes.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {
                fuentes.addLayerToMap()
            } else {
                fuentes.removeLayerFromMap()
            }
        }
    }
}