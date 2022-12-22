package com.appandroid.sagan.bicicadiz.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appandroid.sagan.bicicadiz.getAparcabicisNameCoordinates
import com.appandroid.sagan.bicicadiz.model.Geometry
import com.appandroid.sagan.bicicadiz.model.Properties

class AparcaBicisViewModel: ViewModel() {

    val aparcabicisNameCoordinates = MutableLiveData <MutableMap <MutableList<Geometry>, MutableList<Properties>>>()

    fun getAparcabicisVMCoordinates() {
        val aparcabicisCoord: MutableMap <MutableList<Geometry>, MutableList<Properties>> = getAparcabicisNameCoordinates()
        aparcabicisNameCoordinates.postValue(aparcabicisCoord)
    }
}

