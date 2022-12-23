package com.appandroid.sagan.bicicadiz.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appandroid.sagan.bicicadiz.RetrofitUseCases.getAparcabicisNameCoordinates
import com.appandroid.sagan.bicicadiz.RetrofitUseCases.getCarrilesCoordinates
import com.appandroid.sagan.bicicadiz.RetrofitUseCases.getFuentesCoordinates
import com.appandroid.sagan.bicicadiz.data.model.LineGeometry
import com.appandroid.sagan.bicicadiz.data.model.PointGeometry
import com.appandroid.sagan.bicicadiz.data.model.Properties

class GeodataViewModel: ViewModel() {

    val aparcabicisNameCoordinates = MutableLiveData <MutableMap <MutableList<PointGeometry>, MutableList<Properties>>>()
    val fuentesCoordinates = MutableLiveData<MutableList <PointGeometry>>()
    val carrilesCoordinates = MutableLiveData<MutableList <LineGeometry>>()


    fun getAparcabicisVMCoordinates() {
        val aparcabicisCoord: MutableMap <MutableList<PointGeometry>, MutableList<Properties>> = getAparcabicisNameCoordinates()
        aparcabicisNameCoordinates.postValue(aparcabicisCoord)
    }

    fun getFuentesVMCoordinates() {
        val fuentesCoord: MutableList <PointGeometry> = getFuentesCoordinates()
        fuentesCoordinates.postValue(fuentesCoord)
    }

    fun getCarrilesVMCoordinates() {
        val carrilesCoord: MutableList <LineGeometry> = getCarrilesCoordinates()
        carrilesCoordinates.postValue(carrilesCoord)
    }



}

