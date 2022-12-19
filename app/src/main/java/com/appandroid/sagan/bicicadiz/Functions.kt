package com.appandroid.sagan.bicicadiz

import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.mapbox.pluginscalebar.ScaleBarOptions
import com.mapbox.pluginscalebar.ScaleBarPlugin
import java.io.File

object Functions: AppCompatActivity() {

    fun ImageView.loadUrl(url: String? = null, radius: Int) {
        Glide.with(context)
            .load(url)
            .fitCenter()
            .transform(RoundedCorners(radius))
            .into(this)
    }
}