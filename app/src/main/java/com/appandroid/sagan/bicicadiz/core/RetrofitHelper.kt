package com.appandroid.sagan.bicicadiz.core

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

   fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.mapbox.com/datasets/v1/darenas/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}