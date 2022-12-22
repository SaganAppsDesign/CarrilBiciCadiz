package com.appandroid.sagan.bicicadiz.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appandroid.sagan.bicicadiz.Retrofit.getAparcabicisNameCoordinates
import com.appandroid.sagan.bicicadiz.Retrofit.getFuentesCoordinates
import com.appandroid.sagan.bicicadiz.model.Geometry
import com.appandroid.sagan.bicicadiz.model.Properties

class GeodataViewModel: ViewModel() {

    val aparcabicisNameCoordinates = MutableLiveData <MutableMap <MutableList<Geometry>, MutableList<Properties>>>()
    val fuentesCoordinates = MutableLiveData<MutableList <Geometry>>()


    fun getAparcabicisVMCoordinates() {
        val aparcabicisCoord: MutableMap <MutableList<Geometry>, MutableList<Properties>> = getAparcabicisNameCoordinates()
        aparcabicisNameCoordinates.postValue(aparcabicisCoord)
    }

    fun getFuentesVMCoordinates() {
        val fuentesCoord: MutableList <Geometry> = getFuentesCoordinates()
        fuentesCoordinates.postValue(fuentesCoord)
    }



}

