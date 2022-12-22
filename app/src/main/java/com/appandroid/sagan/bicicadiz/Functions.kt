package com.appandroid.sagan.bicicadiz

import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.appandroid.sagan.bicicadiz.model.Properties
import com.appandroid.sagan.bicicadiz.remote.APIService
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Functions: AppCompatActivity() {

    fun ImageView.loadUrl(url: String? = null, radius: Int) {
        Glide.with(context)
            .load(url)
            .fitCenter()
            .transform(RoundedCorners(radius))
            .into(this)
    }
}


    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.mapbox.com/datasets/v1/darenas/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getAparcabicis(): MutableList<Properties>{
        val nameList = mutableListOf<Properties>()
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getAparcabicis("clbxups790gbo27phj46d8ohk/features?" +
                    "access_token=pk.eyJ1IjoiZGFyZW5hcyIsImEiOiJjbGJrb3ZwOWwwMGcxM3FuMWNqZG5sbnVlIn0.F7SmJXfkGo2xa1-jwdW5fw")
            val aparcaBicis = call.body()

                if(call.isSuccessful){
                    for(i in aparcaBicis?.features?.indices!!){
                        Log.i("aparcaBicis", aparcaBicis.features[i].geometry.coordinates[0].toString())
                        Log.i("aparcaBicis", aparcaBicis.features[i].geometry.coordinates[1].toString())
                        Log.i("aparcaBicis", aparcaBicis.features[i].properties.name)
                        nameList.add(Properties(aparcaBicis.features[i].properties.name))
                    }

            } else{  println("Error")  }
    }
    return nameList
}