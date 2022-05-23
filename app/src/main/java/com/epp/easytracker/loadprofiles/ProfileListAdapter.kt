package com.epp.easytracker.loadprofiles

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.epp.easytracker.database.Profile
import com.epp.easytracker.databinding.ListProfilesBinding


class ProfileListAdapter(private val clickListener: ProfileListener, private val editListener: EditListener, private val deleteListener: DeleteListener) : ListAdapter<Profile, ProfileListAdapter.ViewHolder>(ProfileDiffCallback()){

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener,editListener, deleteListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }



    class ViewHolder private constructor (val binding: ListProfilesBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(clickListener: ProfileListener, editListener: EditListener, deleteListener: DeleteListener, item: Profile) {
            binding.profile = item
            binding.clickListener = clickListener
            binding.editListener = editListener
            binding.deleteListener = deleteListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListProfilesBinding.inflate(layoutInflater, parent,false)

                return ViewHolder(binding)
            }
        }
    }
}

class ProfileDiffCallback: DiffUtil.ItemCallback<Profile>(){
    override fun areContentsTheSame(oldItem: Profile, newItem: Profile): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Profile, newItem: Profile): Boolean {
        return oldItem.profileId == newItem.profileId
    }
}

class ProfileListener(val clickListener: (profileId: Long) -> Unit) {
    fun onClick(profile: Profile) = clickListener(profile.profileId)
}

class EditListener(val editListener: (profileId: Long) -> Unit) {
    fun onEdit(profile: Profile) = editListener(profile.profileId)
}

class DeleteListener(val deleteListener: (profileId: Long) -> Unit) {
    fun onDelete(profile: Profile) = deleteListener(profile.profileId)
}