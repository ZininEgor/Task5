package com.zininegor.task5.fininsh

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.zininegor.task5.others.Case

class FinishViewModel : ViewModel() {

    val database = Firebase.database.reference
    val user = Firebase.auth.currentUser!!
    var list: MutableList<Case> = mutableListOf()
    fun addStats(nicknameOpponent: String, status: String)
    {
        list.add(Case(nickname=user.displayName,nickOpponent=nicknameOpponent, status = status))
        database.child("stats").child(user.email!!.replace(".","")).setValue(list)
    }
}