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
import com.example.assignease.databinding.EmployeesProfileItemviewBinding
import com.example.assignease.fragments.EmployeeFragmentDirections
import com.example.assignease.models.Users
import com.example.assignease.utils.utils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.Objects

class EmployeesAdaptor (private val context: Context): RecyclerView.Adapter<EmployeesAdaptor.EmployeeViewHolder>() {

    class EmployeeViewHolder(val binding: EmployeesProfileItemviewBinding ) :
        ViewHolder(binding.root)


    val diffUtil = object : DiffUtil.ItemCallback<Users>(){
        override fun areItemsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem == newItem
        }
    }

    private var recentlyDeletedItem: Users? = null
    private var recentlyDeletedItemPosition: Int = -1
    private var currentuser = FirebaseAuth.getInstance().currentUser?.uid.toString()
    var isUndergoingProcess = false
    private var recentlyDeletedImageUrl: String? = null


    val differ = AsyncListDiffer(this , diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        return EmployeeViewHolder(EmployeesProfileItemviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val empData = differ.currentList[position]
        holder.binding.apply {

            Glide.with(holder.itemView).load(empData.userImage).into(rvemployeeImage)
            rvemployeeTxt.text = empData.userName
        }

        holder.itemView.setOnClickListener {
            val action = EmployeeFragmentDirections.actionEmployeeFragmentToWorkFragment(empData)
             Navigation.findNavController(it).navigate(action)
        }
    }

    fun deleteItem(position: Int, itemView: View) {
        if(isUndergoingProcess){
            return
        }
        recentlyDeletedItem = differ.currentList[position]
        recentlyDeletedItemPosition = position
        recentlyDeletedImageUrl = recentlyDeletedItem?.userImage

        val newList = differ.currentList.toMutableList()
        newList.removeAt(position)
        differ.submitList(newList)

        removeDataFromDatabase(recentlyDeletedItem!!)

        Snackbar.make(itemView, "Employee deleted", Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                undoDelete()
            }.show()
    }



    private fun undoDelete() {
        if(isUndergoingProcess){
            return
        }
        isUndergoingProcess = true

        recentlyDeletedItem?.let {
            val newList = differ.currentList.toMutableList()
            newList.add(recentlyDeletedItemPosition, it)
            differ.submitList(newList)
            addDataToDatabase(it)

        }
        resetUndoState()
    }

//    private fun restoreUsertoAuth(user: Users) {
//         val auth = FirebaseAuth.getInstance()
//        auth.createUserWithEmailAndPassword(user.userEmail.toString(), user.userPassword.toString())
//            .addOnCompleteListener {task->
//                if(task.isSuccessful){
//                    val firebaseUser = task.result?.user
//                    firebaseUser?.let {
//                        val userProfileChangeRequest = userProfileChangeRequest {
//                            displayName = user.userName
//                            photoUri = Uri.parse(user.userImage)
//                        }
//
//                        it.updateProfile(userProfileChangeRequest)
//                    }
//                }
//                else{
//                    Toast.makeText(context , "Something Error IN backend !!",Toast.LENGTH_SHORT).show()
//
//                }
//            }
//    }

//    private fun restoreProfileImage(userId: String?, recentlyDeletedImageUrl: String) {
//        val databaseref = FirebaseDatabase.getInstance().getReference("Users")
//        val userRef = databaseref.child(userId.toString())
//
//        userRef.child("userImage").setValue(recentlyDeletedImageUrl)
//            .addOnSuccessListener {
//                Toast.makeText(context , "Data is Cleared !",Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener {
//                Toast.makeText(context , "Error : Data is not Cleared !",Toast.LENGTH_SHORT).show()
//            }
//
//        val storageRef = FirebaseStorage.getInstance().reference
//        val imageRef = storageRef.child("Profile/$userId/Profile.jpg")
//
//        imageRef.putFile(Uri.parse(recentlyDeletedImageUrl)).addOnSuccessListener {
//            Toast.makeText(context , "Data is Cleared2 !",Toast.LENGTH_SHORT).show()
//
//        }.addOnFailureListener {
//            Toast.makeText(context , "Error : Data is not Cleared2 !",Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun resetUndoState() {
        recentlyDeletedItem = null
        recentlyDeletedItemPosition = -1
        isUndergoingProcess = false
        recentlyDeletedImageUrl = null
    }

    private fun removeDataFromDatabase(recentlyDeletedItem: Users?) {
        FirebaseDatabase.getInstance().getReference("Users").child(recentlyDeletedItem?.userId.toString()).removeValue()
    }

    private fun addDataToDatabase(it: Users) {
        FirebaseDatabase.getInstance().getReference("Users").child(it.userId.toString()).setValue(it)

    }

//    private fun deleteProfileImage(userId: String?) {
//         val storageRef = FirebaseStorage.getInstance().reference
//        val imageRef = storageRef.child("Profile/$userId/Profile.jpg")
//
//        imageRef.delete().addOnSuccessListener {
//            Toast.makeText(context , "Data is Cleared3!",Toast.LENGTH_SHORT).show()
//        }
//            .addOnFailureListener {
//                Toast.makeText(context , "Error : Data is not Cleared3!",Toast.LENGTH_SHORT).show()
//            }
//    }
//    private fun deleteUserFromAuth(userId: String? , context: Context) {
//        val user = FirebaseAuth.getInstance().currentUser
//        user?.delete()?.addOnCompleteListener {task ->
//            if(task.isSuccessful) {
//                Toast.makeText(context , "Data is Cleared4!",Toast.LENGTH_SHORT).show()
//
//            } else{
//
//                Toast.makeText(context , "Error : Data is not Cleared4 !",Toast.LENGTH_SHORT).show()
//            }
//        }
//    }


}
