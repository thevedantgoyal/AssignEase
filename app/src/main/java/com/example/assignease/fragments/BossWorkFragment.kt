package com.example.assignease.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignease.Auth.SignIn
import com.example.assignease.R
import com.example.assignease.adapters.EmployeeMainAdapter
import com.example.assignease.apiInterface.ApiUtilites
import com.example.assignease.databinding.FragmentBossWorkBinding
import com.example.assignease.models.Notification
import com.example.assignease.models.NotificationData
import com.example.assignease.models.NotificationRequest
import com.example.assignease.models.Users
import com.example.assignease.models.Works
import com.example.assignease.utils.utils
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BossWorkFragment : Fragment() {
    private lateinit var binding : FragmentBossWorkBinding
    private lateinit var employeeMainAdapter: EmployeeMainAdapter
    private lateinit var bossId : String
    val empdata by navArgs<BossWorkFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {


        binding = FragmentBossWorkBinding.inflate(layoutInflater)

        binding.backbtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_bossWorkFragment_to_bossFragment)
        }


        prepareRvforEmpWork()
        showAllEmpWork()


        return binding.root
    }

    private fun prepareRvforEmpWork() {
        Log.d("Boss" , "RV work me  aaya")
        employeeMainAdapter = EmployeeMainAdapter(::onProgressBtnClicked,::onCompletedBtnClicked)
        binding.bossWorksRv.apply {
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.VERTICAL ,false)
            adapter = employeeMainAdapter
        }
    }

    private fun showAllEmpWork() {
        var workdonedialog = false
        Log.d("Boss" , "show all me aaya")
         utils.showdialog(requireContext())

        val empId = FirebaseAuth.getInstance().currentUser?.uid

        Log.d("Boss" , "empid : ${empId}")

//        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.d("Boss" , "on data change me aaya")
//
//                 for(thisboss in snapshot.children){
//
//                     val currentuser = thisboss.getValue(Users::class.java)
//                     Log.d("Boss" , "current user : ${currentuser}")
//                     if(currentuser?.usertype == "Boss"){
//
//                         bossId = currentuser.userId.toString()
//
//                         Log.d("Boss" , "boss id : ${bossId}")
//                     }
//                 }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("Boss" , "on cancelled me aaya")
//               utils.toastmessage(requireContext() , "not get boss userId")
//            }
//        })

        val userid = empdata.bossDetail.usertype
        if(userid == "Boss"){
            bossId = empdata.bossDetail.userId.toString()
        }

        Log.d("Boss" , "boss id : ${bossId}")
        Log.d("Boss" , "boss id : ${userid}")




        val workRoom = empId + bossId

        Log.d("Boss" , "work room : ${workRoom}")

        val workRef = FirebaseDatabase.getInstance()
            workRef.getReference("Works").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Boss" , "on data change 2 me aaya")
                for (worksrooms in snapshot.children) {
                    if (worksrooms.key == workRoom) {

                        if(workdonedialog == true){
                            utils.hidedialog()
                            binding.WorksTv.visibility = View.GONE
                        }
                        Log.d("Boss" , "if me aaya")
                        Log.d("Boss" , "workroom : ${worksrooms.key}")

                        val empWorkRef = workRef.getReference("Works").child(worksrooms.key!!)
                        empWorkRef.addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val worklist = ArrayList<Works>()
                                if(workdonedialog == true){
                                    utils.hidedialog()
                                    binding.WorksTv.visibility = View.GONE
                                }
                                for(worksall in snapshot.children)
                                {
                                    if(workdonedialog == true){
                                        utils.hidedialog()
                                        binding.WorksTv.visibility = View.GONE
                                    }
                                    val works = worksall.getValue(Works::class.java)
                                    worklist.add(works!!)
                                }
                                employeeMainAdapter.differ.submitList(worklist)
                                utils.hidedialog()

                            binding.WorksTv.visibility = if (worklist.isEmpty()) {
                                utils.hidedialog()
                                View.VISIBLE
                            }else{
                                utils.hidedialog()
                                View.GONE
                                 }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("Boss" , "on cancelled me aaya")

                            }

                        })
                    }else{
                        Log.d("employeemain" , "work done")
                        binding.WorksTv.visibility = View.VISIBLE
                        workdonedialog = true
                        utils.hidedialog()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                utils.hidedialog()
                utils.toastmessage(requireContext() , "failed to upload : ${error.message}")
            }
        })
    }

    private fun onProgressBtnClicked(works: Works , progressBtn : MaterialButton) {
        if (progressBtn.text != "In Progress") {
            val builder = AlertDialog.Builder(requireContext())
            val alertDialog = builder.create()

            builder.setTitle("Starting Work")
                .setMessage("Are you starting this work?")
                .setIcon(R.drawable.logout)
                .setPositiveButton("YES") { _, _ ->
                    progressBtn.apply {
                        text = "In Progress"
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.Beige))
                        setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.darkpink
                            )
                        )
                    }
                    updateStatus(works, "2")
                }
                .setNegativeButton("NO") { _, _ ->
                    alertDialog.dismiss()
                }
                .show()
                .setCancelable(false)
        }else{
            utils.toastmessage(requireContext() , "Work is in Progress Or Completed")
        }
    }

    private fun updateStatus(works: Works, status: String) {
        Log.d("Boss" , "show all me aaya")
        utils.showdialog(requireContext())

        val empId = FirebaseAuth.getInstance().currentUser?.uid

//        Log.d("Boss" , "empid : ${empId}")
//        val userid = empdata.bossDetail.usertype
//        if(userid == "Boss"){
//            bossId = empdata.bossDetail.userId.toString()
//        }

//        Log.d("Boss" , "boss id : ${bossId}")
//        Log.d("Boss" , "boss id : ${userid}")
//        val workRoom = empId + bossId

//        Log.d("Boss" , "work room : ${workRoom}")

        val workRef = FirebaseDatabase.getInstance()
        workRef.getReference("Works").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Boss" , "on data change 2 me aaya")
                for (worksrooms in snapshot.children) {
                    if (worksrooms.key?.contains(empId!!)==true) {

                        Log.d("Boss" , "if me aaya")
                        Log.d("Boss" , "workroom : ${worksrooms.key}")

                        val empWorkRef = workRef.getReference("Works").child(worksrooms.key!!)
                        empWorkRef.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(updateWorks in snapshot.children)
                                {
                                    val againWork = updateWorks.getValue(Works::class.java)
                                    if(works.workId == againWork?.workId){
                                        empWorkRef.child(updateWorks.key!!).child("workStatus").setValue(status)
                                    }
                                }
                                utils.hidedialog()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("Boss" , "on cancelled me aaya")

                            }

                        })
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                utils.hidedialog()
                utils.toastmessage(requireContext() , "failed to upload : ${error.message}")
            }
        })

    }

    private fun onCompletedBtnClicked(works: Works , completeBtn : MaterialButton){
        if(completeBtn.text != "WorkDone") {
            val builder = AlertDialog.Builder(requireContext())
            val alertDialog = builder.create()

            builder.setTitle("Completing Work")
                .setMessage("Are you sure to complete this work?")
                .setIcon(R.drawable.logout)
                .setPositiveButton("YES") { _, _ ->
                    completeBtn.apply {
                        text = "WorkDone"
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.Beige))
                        setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                    }
                    updateStatus(works, "3")
                    sendNotification(works.bossId , works.workTitle.toString())
                }
                .setNegativeButton("NO") { _, _ ->
                    alertDialog.dismiss()
                }
                .show()
                .setCancelable(false)
        }else{
            utils.toastmessage(requireContext(),"Work is Completed")
        }
    }


    private fun sendNotification(bossId: String?, worktitle: String) {
        val empDataSnapshot = FirebaseDatabase.getInstance().getReference("Users").child(bossId!!).get()
        empDataSnapshot.addOnSuccessListener {
            val bossDetail = it.getValue(Users::class.java)
            Log.d("notifythe" , "emp deatil : ${bossDetail}")
            val bossToken = bossDetail?.userToken
            Log.d("notifythe" , "emptoken : ${bossToken}")

            val notification = NotificationRequest(message = Notification(token = bossToken , notification =  NotificationData("WORK COMPLETED!" , worktitle)))
            Log.d("notifythe" , "notification : ${notification}")


            val call = ApiUtilites.api.sendNotification(notification)
            call.enqueue(object : Callback<NotificationRequest> {
                override fun onResponse(
                    p0: Call<NotificationRequest>,
                    p1: Response<NotificationRequest>
                ) {
                    if (p1.isSuccessful) {
                        Log.d("notify", "Notification Sent : ${p1}")
                    }
                    else{
                        utils.toastmessage(requireContext() , "message not sent ")
                        Log.d("notify", "Notification is not Sent ")
                        Log.d("notify", "Notification is not Sent : ${p1.errorBody()?.string()}")
                    }
                }
                override fun onFailure(p0: Call<NotificationRequest>, p1: Throwable) {
                    utils.toastmessage(requireContext() , "send message is not  sent ")
                    Log.d("notify", "on failure : ${p1}")
                }
            })
        }
    }
}