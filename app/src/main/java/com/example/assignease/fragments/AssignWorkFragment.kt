package com.example.assignease.fragments

import android.app.DatePickerDialog

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.assignease.R
import com.example.assignease.apiInterface.ApiUtilites
import com.example.assignease.databinding.FragmentAssignWorkBinding
import com.example.assignease.models.Notification
import com.example.assignease.models.NotificationData
import com.example.assignease.models.NotificationRequest
import com.example.assignease.models.Users
import com.example.assignease.models.Works
import com.example.assignease.utils.utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Locale


class AssignWorkFragment : androidx.fragment.app.Fragment() {

   private lateinit var binding:FragmentAssignWorkBinding
   val employeeData by navArgs<AssignWorkFragmentArgs>()



   private var priority = "1"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAssignWorkBinding.inflate(layoutInflater)


        binding.backbtn.setOnClickListener {
           Navigation.findNavController(it).navigate(R.id.action_assignWorkFragment_to_workFragment)
        }


        setPriority()
        setDate()



        binding.workAssignmentBtn.setOnClickListener {
            assignWork()
        }
        return binding.root


    }

    private fun assignWork() {
        utils.showdialog(requireContext())
        val worktitle = binding.workTitle.text.toString()
        val workDes = binding.workDescription.text.toString()
        val workLastDate = binding.dateTxt.text.toString()


       if(worktitle.isEmpty()){
           utils.apply {
               hidedialog()
               toastmessage(requireContext(),"Please Select Work Title!")
           }
       }
        else if(workLastDate == "Last Date : "){
            utils.apply {
                hidedialog()
                toastmessage(requireContext(),"Please select Last Date!")
            }
       }
        else{

            val empId = employeeData.employeeDetail.userId
           val bossId = FirebaseAuth.getInstance().currentUser?.uid
           val workRoom = empId + bossId
           val randomId =
               (1..20).map { (('A'..'Z') + ('a'..'z') + ('0'..'9')).random() }.joinToString("")


           val work = Works(
               workId = randomId,
               bossId = bossId,
               workTitle  = worktitle,
               workDescription = workDes,
               workLastDate = workLastDate,
               workPriority = priority,
               workStatus = "1"

           )

           FirebaseDatabase.getInstance().getReference("Works").child(workRoom).child(randomId).setValue(work).addOnSuccessListener {


               sendNotification(empId , worktitle)
               utils.hidedialog()
               utils.toastmessage(requireContext(),"Work has been assigned to ${employeeData.employeeDetail.userName}")

               val action = AssignWorkFragmentDirections.actionAssignWorkFragmentToWorkFragment(employeeData.employeeDetail)
               findNavController().navigate(action)
           }
       }
    }

    private fun sendNotification(empId: String?, worktitle: String) {
        val empDataSnapshot = FirebaseDatabase.getInstance().getReference("Users").child(empId!!).get()
        empDataSnapshot.addOnSuccessListener {
            val empDetail = it.getValue(Users::class.java)
            Log.d("notifythe" , "emp deatil : ${empDetail}")
            val empToken = empDetail?.userToken
            Log.d("notifythe" , "emptoken : ${empToken}")

            val notification = NotificationRequest(message = Notification(token = empToken , notification =  NotificationData("WORK ASSIGNED" , worktitle)))
            Log.d("notifythe" , "notification : ${notification}")


            val call = ApiUtilites.api.sendNotification(notification)
            call.enqueue(object : Callback<NotificationRequest>{
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

    private fun setDate() {
        val myCalender = Calendar.getInstance()
        val datepicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            myCalender.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLable(myCalender)
            }
        }
        binding.datePicker.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                datepicker,
                myCalender.get(Calendar.YEAR),
                myCalender.get(Calendar.MONTH),
                myCalender.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
    private fun updateLable(myCalender: Calendar) {
        val myformat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myformat, Locale.UK)
        binding.dateTxt.text = sdf.format(myCalender.time)
    }

    private fun setPriority() {
        binding.apply {
            greenOval.setOnClickListener {
                utils.toastmessage(requireContext(),"Priority : Low")
                priority = "1"
                binding.greenOval.setImageResource(R.drawable.correct_signal_svgrepo_com)
                binding.orangeOval.setImageResource(0)
                binding.redOval.setImageResource(0)
            }

            orangeOval.setOnClickListener {
                utils.toastmessage(requireContext(),"Priority : Medium")
                priority = "2"
                binding.greenOval.setImageResource(0)
                binding.orangeOval.setImageResource(R.drawable.correct_signal_svgrepo_com)
                binding.redOval.setImageResource(0)
            }
            redOval.setOnClickListener {
                utils.toastmessage(requireContext(),"Priority : High")
                priority = "3"
                binding.greenOval.setImageResource(0)
                binding.orangeOval.setImageResource(0)
                binding.redOval.setImageResource(R.drawable.correct_signal_svgrepo_com)
            }
        }
    }



}


