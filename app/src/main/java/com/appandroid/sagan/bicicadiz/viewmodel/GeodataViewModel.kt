package com.appandroid.sagan.bicicadiz.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appandroid.sagan.bicicadiz.Retrofit.getAparcabicisNameCoordinates
import com.appandroid.sagan.bicicadiz.Retrofit.getCarrilesCoordinates
import com.appandroid.sagan.bicicadiz.Retrofit.getFuentesCoordinates
import com.appandroid.sagan.bicicadiz.model.LineGeometry
import com.appandroid.sagan.bicicadiz.model.PointGeometry
import com.appandroid.sagan.bicicadiz.model.Properties

class GeodataViewModel: ViewModel() {

    val aparcabicisNameCoordinates = MutableLiveData <MutableMap <MutableList<PointGeometry>, MutableList<Properties>>>()
    val fuentesCoordinates = MutableLiveData<MutableList <PointGeometry>>()
    val carrilesCoordinates = MutableLiveData<MutableList <MutableList <LineGeometry>>>()


    fun getAparcabicisVMCoordinates() {
        val aparcabicisCoord: MutableMap <MutableList<PointGeometry>, MutableList<Properties>> = getAparcabicisNameCoordinates()
        aparcabicisNameCoordinates.postValue(aparcabicisCoord)
    }

    fun getFuentesVMCoordinates() {
        val fuentesCoord: MutableList <PointGeometry> = getFuentesCoordinates()
        fuentesCoordinates.postValue(fuentesCoord)
    }

    fun getCarrilesVMCoordinates() {
        val carrilesCoord: MutableList <MutableList <LineGeometry>> = getCarrilesCoordinates()
        carrilesCoordinates.postValue(carrilesCoord)
    }



}

