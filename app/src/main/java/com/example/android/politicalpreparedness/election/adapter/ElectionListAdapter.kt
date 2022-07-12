package com.example.android.politicalpreparedness.election.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.ElectionListItemBinding
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "PPElectionLA"

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
            binding.election = item
            binding.electionClicked = clickListener
            binding.voterInfo = item.voterInfo
            Log.d(TAG, "item.expanded: ${item.expanded}")
            binding.expandedVoterInfo.visibility = when (item.expanded) {
                true -> View.VISIBLE
                else -> View.GONE
            }

            val electionDao = ElectionDatabase.getInstance(binding.root.context).electionDao
            binding.btnFollow.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val savedElection = electionDao.getElection(item.id)
                    if (savedElection != null) {
                        electionDao.deleteByElectionId(item.id)
                    } else {
                        item.voterInfo?.let { electionDao.insertElection(it.election) }
                    }
                }
            }



//            if (item.expanded == true) {
//                binding.expandedVoterInfo.setVisibility(View.VISIBLE)
//            } else {
//                binding.expandedVoterInfo.animate()
//                    .alpha(0.0f)
//                    .setDuration(300)
//                    .setListener(object : AnimatorListenerAdapter() {
//                        override fun onAnimationEnd(animation: Animator) {
//                            super.onAnimationEnd(animation)
//                            binding.expandedVoterInfo.setVisibility(View.GONE)
//                        }
//                    })
//            }

            binding.executePendingBindings()
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

class ElectionClickListener(val electionFunctionBlock: (Election) -> Unit) {
    fun onClick(election: Election) = electionFunctionBlock(election)
}

@BindingAdapter("android:visibility")
fun ImageView.setVisibility(visible: Boolean) {
    if (visible) visibility = View.VISIBLE
    else visibility = View.GONE
}