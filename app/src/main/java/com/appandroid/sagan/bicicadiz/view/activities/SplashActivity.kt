package com.appandroid.sagan.bicicadiz.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appandroid.sagan.bicicadiz.databinding.ActivityPantallaInicioBinding

private lateinit var binding : ActivityPantallaInicioBinding

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.animationView?.playAnimation()
        binding.animationView?.repeatCount = 10
        binding.animationView?.alpha = 0.8F

        binding.btSiguiente.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
     }
  }


