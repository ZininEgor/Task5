package com.zininegor.task5.waitroomclient

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WaitRoomClientViewModel : ViewModel() {

    var doneNavigation = MutableLiveData<Boolean>()
    var doneNavigationToGame = MutableLiveData<Boolean>()

    init {
        doneNavigationToGame.value = false
        doneNavigation.value = false
    }


}