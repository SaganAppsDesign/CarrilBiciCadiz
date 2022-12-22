package com.appandroid.sagan.bicicadiz.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appandroid.sagan.bicicadiz.getAparcabicis
import com.appandroid.sagan.bicicadiz.model.Geometry
import com.appandroid.sagan.bicicadiz.model.MainResponse
import com.appandroid.sagan.bicicadiz.model.Properties

class AparcaBicisViewModel: ViewModel() {

    val aparcabicisModel = MutableLiveData<MutableList<Properties>>()

    fun getAparcabicisViewModel() {
        val aparcabicisResponse: MutableList<Properties> = getAparcabicis()
        aparcabicisModel.postValue(aparcabicisResponse)
    }
}

