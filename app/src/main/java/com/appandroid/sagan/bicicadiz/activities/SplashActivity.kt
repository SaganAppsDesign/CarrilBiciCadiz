package com.appandroid.sagan.bicicadiz.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appandroid.sagan.bicicadiz.BuildConfig

import com.appandroid.sagan.bicicadiz.databinding.ActivityPantallaInicioBinding

private lateinit var binding : ActivityPantallaInicioBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.animationView.playAnimation()
        binding.animationView.repeatCount = 10
        binding.animationView.alpha = 0.8F

        if(BuildConfig.FLAVOR == "carrilBiciMalaga"){
            binding.tvTitle2.text = BuildConfig.TITLE_SPLASH
        }

        binding.btSiguiente.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
     }
  }


