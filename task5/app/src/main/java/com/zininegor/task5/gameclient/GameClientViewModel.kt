package com.zininegor.task5.gameclient

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.zininegor.task5.game.MyMap
import com.zininegor.task5.others.EndGame
import com.zininegor.task5.others.Step
import com.zininegor.task5.others.elementIsEmpty

class GameClientViewModel : ViewModel() {
    var list = MutableLiveData<List<MyMap>>()
    var win = false
    var draw = false
    val database = Firebase.database.reference
    var stepOwner = MutableLiveData<String>()
    var navigateToFinish = MutableLiveData<Boolean>()


    fun finish(endGame: EndGame) {
        if (endGame.draw) {
            draw = true
        }

        if (endGame.winner == "O") {
            win = true
        }

        navigateToFinish.value = true
    }

    init {
        navigateToFinish.value = false
    }

    var roomId = ""
    fun onClicked(myMap: MyMap) {

        if (elementIsEmpty(myMap.map!!.keys.first(), list.value!!)) {
            if (stepOwner.value == "client") {

                Log.i("Adapter", myMap.toString())
                database.child(roomId.replace(".", "")).child("GameData").child("matrix")
                    .child((myMap.map!!.keys.first().replace("_", "").toInt() - 1).toString())
                    .setValue(
                        mapOf(myMap.map!!.keys.first().toString() to "O")
                    )
                database.child(roomId).child("GameStep")
                    .setValue(Step(stepOwner = "host"))
            }
        }
    }
}