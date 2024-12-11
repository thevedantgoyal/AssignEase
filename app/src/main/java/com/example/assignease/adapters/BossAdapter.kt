package com.example.assignease.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.assignease.databinding.BossProfileItemViewBinding
import com.example.assignease.databinding.EmployeesProfileItemviewBinding
import com.example.assignease.fragments.BossFragmentDirections
import com.example.assignease.fragments.EmployeeFragmentDirections
import com.example.assignease.models.Users
import com.example.assignease.models.Works
import com.example.assignease.utils.utils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.Objects

class BossAdapter (private val context: Context): RecyclerView.Adapter<BossAdapter.BossViewHolder>() {

    class BossViewHolder(val binding: BossProfileItemViewBinding) :
        ViewHolder(binding.root)


    val diffUtil = object : DiffUtil.ItemCallback<Users>() {
        override fun areItemsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BossViewHolder {
        return BossViewHolder(
            BossProfileItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    override fun onBindViewHolder(holder: BossViewHolder, position: Int) {
        val bossData = differ.currentList[position]



        holder.binding.apply {
            Glide.with(holder.itemView).load(bossData.userImage).into(rvbossImage)
            rvbossTxt.text = bossData.userName


        }
        holder.itemView.setOnClickListener {

            Navigation.findNavController(it)
                .navigate(BossFragmentDirections.actionBossFragmentToBossWorkFragment(bossData))
        }
    }
}