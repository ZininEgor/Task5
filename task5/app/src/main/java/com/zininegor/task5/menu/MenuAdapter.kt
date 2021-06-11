package com.zininegor.task5.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zininegor.task5.databinding.ListItemStatsBinding
import com.zininegor.task5.others.Case

class StatAdapter : ListAdapter<Case, StatAdapter.ViewHolder>(CycleDiffCallback()) {
    override fun onBindViewHolder(holder: StatAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatAdapter.ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemStatsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Case) {
            binding.sleep = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemStatsBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class CycleDiffCallback : DiffUtil.ItemCallback<Case>() {
    override fun areItemsTheSame(oldItem: Case, newItem: Case): Boolean {
        return oldItem.nickname == newItem.nickname
    }

    override fun areContentsTheSame(oldItem: Case, newItem: Case): Boolean {
        return oldItem == newItem
    }
}