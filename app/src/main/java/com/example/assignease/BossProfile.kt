package com.example.assignease

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.assignease.databinding.FragmentBossProfileBinding

import com.example.assignease.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BossProfile : Fragment() {
    private lateinit var binding: FragmentBossProfileBinding
    private lateinit var profileImage : ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBossProfileBinding.inflate(layoutInflater)

         profileImage = binding.profileImg



        
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("currentuser" , "currrent user : ${currentUser}")

        val allData = FirebaseDatabase.getInstance().getReference("Users").child(currentUser!!).addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val usersData = snapshot.getValue(Users::class.java)

                    Glide.with(this@BossProfile).load(usersData?.userImage).into(profileImage)
                    if (usersData != null) {
                        binding.profileName.text = usersData.userName
                        binding.profileEmail.text = usersData.userEmail
                        binding.profilePhone.text = usersData.userPhoneNo
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.backbtn.setOnClickListener {
          Navigation.findNavController(it).navigate(R.id.action_homeProfile_to_employeeFragment)
        }
        
        return binding.root
    }
}