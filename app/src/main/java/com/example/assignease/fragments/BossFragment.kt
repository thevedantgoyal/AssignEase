package com.example.assignease.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.assignease.Auth.SignIn
import com.example.assignease.R
import com.example.assignease.adapters.BossAdapter
import com.example.assignease.databinding.FragmentBossBinding
import com.example.assignease.models.Users
import com.example.assignease.utils.utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BossFragment : Fragment() {

 private lateinit var binding: FragmentBossBinding
 private lateinit var bossAdapter: BossAdapter
    private lateinit var imageView : ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBossBinding.inflate(layoutInflater)

        binding.logoutBtn3.setOnClickListener {
            showLogoutdialog()
        }

        prepareRvBossAdapter()
        showAllBoss()


        imageView = binding.EmpProfileImg
        val id = FirebaseAuth.getInstance().currentUser?.uid

        val database = FirebaseDatabase.getInstance().getReference("Users").child(id!!).addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val userUrl = snapshot.getValue(Users::class.java)
                    Glide.with(this@BossFragment).load(userUrl?.userImage).into(imageView)
                    Log.d("glide", "IMAGE IS UPLOADED")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("glide", "IMAGE IS NOT UPLOADED")
            }
        })

        binding.EmpProfileImg.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_bossFragment_to_employeeProfile2)
        }


        return binding.root
    }

    private fun showAllBoss() {
        utils.showdialog(requireContext())
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val bossList = arrayListOf<Users>()
                for(bosses in snapshot.children){
                    val currentUser = bosses.getValue(Users::class.java)
                    if(currentUser?.usertype == "Boss"){
                        bossList.add(currentUser)
                    }
                }
                bossAdapter.differ.submitList(bossList)
                utils.hidedialog()

            }

            override fun onCancelled(error: DatabaseError) {
               utils.hidedialog()
                utils.toastmessage(requireContext(),"boss upload failed : ${error.message}")
            }

        })

    }

    private fun prepareRvBossAdapter() {
        bossAdapter = BossAdapter(requireContext())
        binding.bossRv.apply {
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.VERTICAL ,false)
            adapter = bossAdapter
        }
    }

    private fun showLogoutdialog() {
        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = builder.create()

        builder.setTitle("LogOut")
            .setMessage("Are you sure you want to Logout!")
            .setIcon(R.drawable.logout)
            .setPositiveButton("YES"){_,_->
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(requireContext(), SignIn::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("NO"){_,_->
                alertDialog.dismiss()
            }
            .show()
            .setCancelable(false)
    }



}