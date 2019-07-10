package com.appandroid.sagan.bicicadiz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast

class PantallaInicio : AppCompatActivity() {

    private val progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_inicio)



    }


    fun clickMapa (view : View){

        val intent = Intent(this, MainActivity::class.java)

        Toast.makeText(this, "Bienvenidos a la APP Carirril Bici,", Toast.LENGTH_LONG)

        startActivity(intent)

        finish()

    }

}


