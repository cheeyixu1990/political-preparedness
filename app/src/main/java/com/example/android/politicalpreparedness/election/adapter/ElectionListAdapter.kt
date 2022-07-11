package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ElectionListItemBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionClickListener): ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(ElectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item!!)
    }

    class ElectionViewHolder private constructor (private val binding: ElectionListItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(clickListener: ElectionClickListener, item: Election) {
//            binding.asteroid = item
//            binding.clickableOverlay.contentDescription = String.format("%s is approaching on %s, it is %s", item.codename, item.closeApproachDate, when(item.isPotentiallyHazardous) {
//                true -> "potentially hazardous."
//                else -> "safe."
//            })
//            binding.asteroidClicked = clickListener
//            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ElectionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ElectionListItemBinding.inflate(layoutInflater, parent, false)

                return ElectionViewHolder(binding)
            }
        }
    }
}

class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }
}

class ElectionClickListener(val asteroidFunctionBlock: (Election) -> Unit) {
    fun onClick(asteroid: Election) = asteroidFunctionBlock(asteroid)
}