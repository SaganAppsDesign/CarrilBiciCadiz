package com.appandroid.sagan.bicicadiz

import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

object Functions: AppCompatActivity() {

    fun loadJsonFromAsset(filename: String): String? {
        return try {
            val `is` = assets.open(filename)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()

            String(buffer)

        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

}