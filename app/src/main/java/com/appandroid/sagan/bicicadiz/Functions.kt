package com.appandroid.sagan.bicicadiz

import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.appandroid.sagan.bicicadiz.fragments.WelcomeInfoFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

object Functions: AppCompatActivity() {

    fun ImageView.loadUrl(url: String? = null, radius: Int) {
        Glide.with(context)
            .load(url)
            .fitCenter()
            .transform(RoundedCorners(radius))
            .into(this)
    }
}

