package com.appandroid.sagan.bicicadiz.remote

import com.appandroid.sagan.bicicadiz.data.model.GeoLineResponse
import com.appandroid.sagan.bicicadiz.data.model.GeoPointResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun getPointData(@Url url:String):Response<GeoPointResponse>

    @GET
    suspend fun getLineData(@Url url:String):Response<GeoLineResponse>
}