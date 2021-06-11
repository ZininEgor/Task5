package com.zininegor.task5.others

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Case(
    var nickname: String? = "",
    var status: String? = "",
    var nickOpponent: String? = "",
)

@IgnoreExtraProperties
data class Host(
    var nickname: String? = "",
    var email: String? = "",
    var status: String? = "",
    var urlAvatar: String? = "",
)

@IgnoreExtraProperties
data class Client(
    var nickname: String? = "",
    var email: String? = "",
    var status: String? = "",
    var urlAvatar: String? = "",
)

@IgnoreExtraProperties
data class Step(
    var stepOwner: String = ""
)

@IgnoreExtraProperties
data class GameData(
    var matrix: List<Map<String, String>>? = listOf(
        mapOf(
            "1_" to "",
            "2_" to "",
            "3_" to "",
            "4_" to "",
            "5_" to "",
            "6_" to "",
            "7_" to "",
            "8_" to "",
            "9_" to "",
        )
    )
)

@IgnoreExtraProperties
data class EndGame(
    var winner: String = "",
    var loser: String = "",
    var draw: Boolean = false,
    var navigate: Boolean = true
)