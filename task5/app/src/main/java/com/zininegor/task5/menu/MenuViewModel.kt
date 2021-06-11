package com.zininegor.task5.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zininegor.task5.others.Case

class MenuViewModel : ViewModel() {

    var doneNavigation = MutableLiveData<Boolean>()
    var list = MutableLiveData<MutableList<Case>>()

    init {

        doneNavigation.value = false
    }
}