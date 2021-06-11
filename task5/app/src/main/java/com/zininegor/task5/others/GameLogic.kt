package com.zininegor.task5.others

import android.util.Log
import com.zininegor.task5.game.MyMap

fun elementIsEmpty(key: String, list: List<MyMap>): Boolean {
    val key = key.replace("_", "").toInt() - 1
    Log.i("checkKey", key.toString())
    if (list[key].map!!.values.first().isEmpty()) {
        return true
    }
    return false
}

fun GameLogic(rawlist: List<MyMap>): String {

    val matrix = Array(3) { Array(3) { "" } }
    matrix[0] = arrayOf(
        rawlist[0].map!!.values.first(),
        rawlist[1].map!!.values.first(),
        rawlist[2].map!!.values.first()
    )
    matrix[1] = arrayOf(
        rawlist[3].map!!.values.first(),
        rawlist[4].map!!.values.first(),
        rawlist[5].map!!.values.first()
    )
    matrix[2] = arrayOf(
        rawlist[6].map!!.values.first(),
        rawlist[7].map!!.values.first(),
        rawlist[8].map!!.values.first()
    )

    fun verticalCheck(): String {
        for (i in 0..2) {
            if ((matrix[i][0] == matrix[i][1] && matrix[i][1] == matrix[i][2] && matrix[i][0] == matrix[i][2]) && matrix[i][2].isNotEmpty()) {
                return matrix[i][0]
            }
        }
        return ""
    }

    fun horizontalCheck(): String {
        for (i in 0..2) {
            if ((matrix[0][i] == matrix[1][i] && matrix[1][i] == matrix[2][i] && matrix[0][i] == matrix[2][i]) && matrix[2][i].isNotEmpty()) {
                return matrix[0][i]
            }
        }
        return ""
    }

    fun diagonalsCheck(): String {

        if ((matrix[0][0] == matrix[1][1] && matrix[1][1] == matrix[2][2] && matrix[2][2] == matrix[0][0]) and matrix[0][0].isNotEmpty()) {
            return matrix[0][0]
        }
        if ((matrix[0][2] == matrix[1][1] && matrix[2][0] == matrix[1][1] && matrix[2][0] == matrix[0][2]) and matrix[0][2].isNotEmpty()) {
            return matrix[0][2]
        }
        return ""
    }

    fun deadHeat(): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {

                Log.i("deadHeat", i.toString() + " " + j.toString() + ":" + matrix[i][j].isEmpty())
                if (matrix[i][j].isEmpty()) {
                    return false
                }
            }
        }
        return true
    }

    if (verticalCheck().isNotEmpty()) {
        return verticalCheck()
    }
    if (horizontalCheck().isNotEmpty()) {
        return horizontalCheck()
    }
    if (diagonalsCheck().isNotEmpty()) {
        return diagonalsCheck()
    }
    if (deadHeat()) {
        return "dead heat"
    }

    return ""
}