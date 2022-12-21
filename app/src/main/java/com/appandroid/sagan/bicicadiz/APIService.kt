package com.appandroid.sagan.bicicadiz

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun getAparcabicis(@Url url:String):Response<MainResponse>
}