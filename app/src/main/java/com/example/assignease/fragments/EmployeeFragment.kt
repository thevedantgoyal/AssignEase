package com.example.assignease.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignease.Auth.SignIn
import com.example.assignease.Auth.SignUp
import com.example.assignease.R
import com.example.assignease.adapters.EmployeesAdaptor
import com.example.assignease.databinding.FragmentEmployeeBinding
import com.example.assignease.models.SwipeDelete
import com.example.assignease.models.Users
import com.example.assignease.utils.utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class EmployeeFragment : Fragment() {
    private lateinit var binding : FragmentEmployeeBinding
    private lateinit var imageView : ImageView

    private lateinit var employeesAdaptor: EmployeesAdaptor
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmployeeBinding.inflate(layoutInflater)


       binding.logoutBtn1.setOnClickListener {
           showLogoutdialog()
       }

        imageView = binding.BossProfileImg
        val id = FirebaseAuth.getInstance().currentUser?.uid

        val database = FirebaseDatabase.getInstance().getReference("Users").child(id!!).addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val userUrl = snapshot.getValue(Users::class.java)
                    Glide.with(this@EmployeeFragment).load(userUrl?.userImage).into(imageView)
                    Log.d("glide", "IMAGE IS UPLOADED")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("glide", "IMAGE IS NOT UPLOADED")
            }
        })


        binding.BossProfileImg.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_employeeFragment_to_homeProfile)
        }


        prepareRvEmployeeAdaptor()
        showAllEmployees()


        return binding.root
    }

    private fun showAllEmployees() {
        utils.showdialog(requireContext())
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val emplist = arrayListOf<Users>()
                for (employees in snapshot.children){
                    val currentuser = employees.getValue(Users::class.java)
                    if(currentuser?.usertype == "Employee"){
                        emplist.add(currentuser)
                    }
                }
                employeesAdaptor.differ.submitList(emplist)
                utils.hidedialog()
            }

            override fun onCancelled(error: DatabaseError) {
                     utils.apply {
                         hidedialog()
                         toastmessage(requireContext(),"failed : ${error.message}")
                     }
            }

        })
    }

    private fun prepareRvEmployeeAdaptor() {
        employeesAdaptor = EmployeesAdaptor(requireContext())
        binding.employeesRv.apply {
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.VERTICAL,false)
            adapter = employeesAdaptor
        }
        val itemTouchHelper = ItemTouchHelper(SwipeDelete(employeesAdaptor))
        itemTouchHelper.attachToRecyclerView(binding.employeesRv)
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