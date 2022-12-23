package com.appandroid.sagan.bicicadiz

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.appandroid.sagan.bicicadiz.model.LineGeometry
import com.appandroid.sagan.bicicadiz.model.PointGeometry
import com.appandroid.sagan.bicicadiz.model.Properties
import com.appandroid.sagan.bicicadiz.remote.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit: AppCompatActivity() {

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.mapbox.com/datasets/v1/darenas/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getAparcabicisNameCoordinates(): MutableMap<MutableList<PointGeometry>, MutableList<Properties>>{
        val coordinatesList = mutableListOf<PointGeometry>()
        val nameList = mutableListOf<Properties>()
        val mapNameCoordinates = mutableMapOf(Pair(coordinatesList, nameList))

        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getPointData("clbxups790gbo27phj46d8ohk/features?" +
                    "access_token=pk.eyJ1IjoiZGFyZW5hcyIsImEiOiJjbGJrb3ZwOWwwMGcxM3FuMWNqZG5sbnVlIn0.F7SmJXfkGo2xa1-jwdW5fw")
            val aparcaBicis = call.body()

            if(call.isSuccessful){
                for(i in aparcaBicis?.features?.indices!!){
                    coordinatesList.add(PointGeometry(aparcaBicis.features[i].geometry.coordinates))
                    nameList.add(Properties(aparcaBicis.features[i].properties.name))
                }
            } else{ println("Error")  }
        }
        return mapNameCoordinates
    }

    fun getFuentesCoordinates(): MutableList<PointGeometry>{
        val pointGeometryList = mutableListOf<PointGeometry>()

        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getPointData("clbxumz5r014k28ozsad9kwwb/features?" +
                    "access_token=pk.eyJ1IjoiZGFyZW5hcyIsImEiOiJjbGJrb3ZwOWwwMGcxM3FuMWNqZG5sbnVlIn0.F7SmJXfkGo2xa1-jwdW5fw")
            val fuentes = call.body()

            if(call.isSuccessful){
                for(i in fuentes?.features?.indices!!){
                    pointGeometryList.add(PointGeometry(fuentes.features[i].geometry.coordinates))
               }
            } else{ println("Error")}
        }
        return pointGeometryList
    }

    fun getCarrilesCoordinates(): MutableList<LineGeometry>{
        val lineGeometryList = mutableListOf<LineGeometry>()

        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getLineData("clc0fim26026g2docli79zgaf/features?" +
                    "access_token=pk.eyJ1IjoiZGFyZW5hcyIsImEiOiJjbGJrb3ZwOWwwMGcxM3FuMWNqZG5sbnVlIn0.F7SmJXfkGo2xa1-jwdW5fw")
            val carriles = call.body()
            Log.i("(carriles","$carriles")
            if(call.isSuccessful){
                for(i in carriles?.features?.indices!!){
                    lineGeometryList.add(LineGeometry(carriles.features[i].geometry.coordinates))
                }
            Log.i("(coordinatesList bucle","$lineGeometryList")
            } else{ println("Error")}
        }
        Log.i("(coordinatesList)","$lineGeometryList")
        return lineGeometryList
    }
}