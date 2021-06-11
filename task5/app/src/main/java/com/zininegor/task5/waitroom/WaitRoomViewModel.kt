package com.zininegor.task5.waitroom

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.zininegor.task5.others.EndGame
import com.zininegor.task5.others.GameData
import com.zininegor.task5.others.Host

class WaitRoomViewModel : ViewModel() {


    private val user = Firebase.auth.currentUser!!

    var doneNavigation = MutableLiveData<Boolean>()

    init {
        doneNavigation.value = false
    }

    private val roomId = user?.email?.replace(".", "")
    val database = Firebase.database.reference


    private fun writeHost(roomId: String, email: String, nickname: String) {
        val host =
            Host(nickname, email, status = "Ready", user.photoUrl.toString())
        database.child(roomId).child("Host").setValue(host)
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

    init {
        database.child(user.email!!.replace(".", "")).child("GameEnd")
            .setValue(EndGame(navigate = false))
        writeHost(roomId!!, user.email!!, user.displayName!!)
    }

}

