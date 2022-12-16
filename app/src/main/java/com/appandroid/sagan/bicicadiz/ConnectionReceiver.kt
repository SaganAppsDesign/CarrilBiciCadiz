package com.appandroid.sagan.bicicadiz

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast

class ConnectionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        val hasConnection = networkInfo != null && networkInfo.isConnected
        if(!hasConnection) {
            Toast.makeText(context, "No tienes conexión. Puede que la app no funcione correctamente", Toast.LENGTH_LONG).show()
        }
    }
}