package com.appandroid.sagan.bicicadiz

import android.app.Application
import com.google.android.gms.ads.MobileAds

class CarrilBiciApp: Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }

}