package com.appandroid.sagan.bicicadiz

import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.appandroid.sagan.bicicadiz.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError

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
}