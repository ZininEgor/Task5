package com.zininegor.task5.game

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.zininegor.task5.R
import com.zininegor.task5.others.Case


@BindingAdapter("key")
fun TextView.key(item: MyMap) {
    item.let {

        if(item.map!!.values.first() == "O")
        {
            setTextColor(resources.getColor(R.color.yellow))
        }
        else
        {
            setTextColor(resources.getColor(R.color.danger))
        }
        text = item.map!!.values.first()
    }
}

@BindingAdapter("nick")
fun TextView.nick(item: Case) {
    item.let {
        text = item.nickname
    }
}

@BindingAdapter("nickOpponent")
fun TextView.nickOpponent(item: Case) {
    item.let {
        text = item.nickOpponent
    }
}

@BindingAdapter("win")
fun TextView.win(item: Case) {
    item.let {

        if(item.status == "Win")
        {
            setTextColor(resources.getColor(R.color.success_dark))
        }
        if(item.status == "Lose")
        {
            setTextColor(resources.getColor(R.color.danger))
        }
        if(item.status == "Draw")
        {
            setTextColor(resources.getColor(R.color.yellow))
        }
        text = item.status
    }
}



