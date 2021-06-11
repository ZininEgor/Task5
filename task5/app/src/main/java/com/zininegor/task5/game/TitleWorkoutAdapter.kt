package com.zininegor.task5.game

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zininegor.task5.databinding.ListItemGameBinding


data class MyMap(
    var map: Map<String, String>?
)

class TitleWorkoutAdapter(val clickListener: TitleWorkoutListener) : ListAdapter<MyMap,
        TitleWorkoutAdapter.ViewHolder>(TitleWorkoutDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i("egor", "work")
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemGameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: TitleWorkoutListener, item: MyMap) {
            binding.sleep = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemGameBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class TitleWorkoutDiffCallback : DiffUtil.ItemCallback<MyMap>() {
    override fun areItemsTheSame(oldItem: MyMap, newItem: MyMap): Boolean {
        return oldItem.map!!.keys.first() == newItem.map!!.keys.first()
    }

    override fun areContentsTheSame(oldItem: MyMap, newItem: MyMap): Boolean {
        return oldItem == newItem
    }
}

class TitleWorkoutListener(val clickListener: (sleepId: MyMap) -> Unit) {
    fun onClick(night: MyMap) = clickListener(night)
}