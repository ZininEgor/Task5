package com.zininegor.task5.game

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.zininegor.task5.others.*

class GameHostViewModel : ViewModel() {

    var list = MutableLiveData<List<MyMap>>()
    var stepOwner = MutableLiveData<String>()
    var win = false
    var draw = false
    var navigateToFinish = MutableLiveData<Boolean>()
    private val user = Firebase.auth.currentUser!!
    fun onClicked(myMap: MyMap) {
        if (elementIsEmpty(myMap.map!!.keys.first(), list.value!!)) {
            if (stepOwner.value == "host") {

                Log.i("Adapter", myMap.toString())
                database.child(user.email!!.replace(".", "")).child("GameData").child("matrix")
                    .child((myMap.map!!.keys.first().replace("_", "").toInt() - 1).toString())
                    .setValue(
                        mapOf(myMap.map!!.keys.first().toString() to "X")
                    )
                database.child(user.email!!.replace(".", "")).child("GameStep")
                    .setValue(Step(stepOwner = "client"))
            }
        }

    }

    fun finish() {
        navigateToFinish.value = true
    }

    fun CreateGame(roomId: String) {
        val defValue = listOf(
            mapOf("1_" to ""),
            mapOf("2_" to ""),
            mapOf("3_" to ""),
            mapOf("4_" to ""),
            mapOf("5_" to ""),
            mapOf("6_" to ""),
            mapOf("7_" to ""),
            mapOf("8_" to ""),
            mapOf("9_" to "")
        )
        val matrix = GameData(matrix = defValue)
        database.child(roomId).child("GameData").setValue(matrix)
    }

    fun finish(endGame: EndGame) {
        if (endGame.draw) {
            draw = true
        }

        if (endGame.winner == "X") {
            win = true
        }

        navigateToFinish.value = true

    }


    init {
        val database = Firebase.database.reference
        database.child(user.email!!.replace(".", "")).child("GameStep")
            .setValue(Step(stepOwner = "host"))
    }


    val database = Firebase.database.reference


}