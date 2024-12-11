package com.example.assignease.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignease.R
import com.example.assignease.adapters.WorksAdapter
import com.example.assignease.databinding.FragmentWorkBinding
import com.example.assignease.models.Works
import com.example.assignease.utils.utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WorkFragment : Fragment() {

    private lateinit var binding : FragmentWorkBinding
    private lateinit var worksAdapter: WorksAdapter
    private lateinit var workRoom : String
    val empData by navArgs<WorkFragmentArgs>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkBinding.inflate(layoutInflater)
        val empDatatoPass = empData.employeeData

        binding.favAssignbtn.setOnClickListener {
            val action = WorkFragmentDirections.actionWorkFragmentToAssignWorkFragment(empDatatoPass)
            findNavController().navigate(action)
        }


        val empname = empData.employeeData.userName

        binding.worksTxt.text = empname.toString()



        binding.backbtn.setOnClickListener {


            Navigation.findNavController(it).navigate(R.id.action_workFragment_to_employeeFragment)







        }



        prepareWorkAdapter()
        showAllWorks()

        return binding.root
    }

    private fun showAllWorks() {
        utils.showdialog(requireContext())
        val bossId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("Work" , "bossId : ${bossId}")
         workRoom = empData.employeeData.userId + bossId

        Log.d("Work" , "workroom : ${workRoom}")

        FirebaseDatabase.getInstance().getReference("Works").child(workRoom)
            .addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                Log.d("Work" , "on data change me aaya ")

              val worklist = ArrayList<Works>()

                for(allWorks in snapshot.children){
                    val work = allWorks.getValue(Works::class.java)
                    Log.d("Work" , "works value : ${work} ")
                    worklist.add(work!!)
                }

                worksAdapter.differ.submitList(worklist)
                Log.d("Work" , "workslist me add ho  gya ${worksAdapter.differ.submitList(worklist)} ")
                utils.hidedialog()

              binding.worksTv.visibility = if(worklist.isEmpty()){
                  View.VISIBLE
              }
              else {
                  View.GONE
              }
            }

            override fun onCancelled(error: DatabaseError) {

                Log.d("Work" , "on data cancelled me aaya ")
               utils.hidedialog()
                utils.toastmessage(requireContext() , "Error : ${error.message}")
            }

        })
    }

    private fun prepareWorkAdapter() {
        worksAdapter = WorksAdapter(::OnUnassignBtnClicked)
        binding.worksRv.apply {
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.VERTICAL,false)
            adapter = worksAdapter
        }
    }

    private fun OnUnassignBtnClicked(works: Works){
        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = builder.create()

        builder.setTitle("Unassigned Work")
            .setMessage("Are you sure you want to unassigned this work!")
            .setPositiveButton("YES"){_,_->
               unassigned(works)
            }
            .setNegativeButton("NO"){_,_->
                alertDialog.dismiss()
            }
            .show()

    }

    private fun unassigned(works: Works) {
        works.expanded = !works.expanded
        FirebaseDatabase.getInstance().getReference("Works").child(workRoom)
            .addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(thework in snapshot.children){
                    val currentWork = thework.getValue(Works::class.java)
                    if(currentWork == works){
                        thework.ref.removeValue()
                        utils.toastmessage(requireContext() , "Work has been Unassigned!")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                utils.hidedialog()
                utils.toastmessage(requireContext() , "Work has not been unassigned")
            }

        })
    }
}
