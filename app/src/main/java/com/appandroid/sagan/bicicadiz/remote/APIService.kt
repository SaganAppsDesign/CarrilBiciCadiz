package com.appandroid.sagan.bicicadiz.remote

import com.appandroid.sagan.bicicadiz.model.MainResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun getGeoData(@Url url:String):Response<MainResponse>
}