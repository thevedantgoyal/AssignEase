package com.example.assignease.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.assignease.BossMainActivity
import com.example.assignease.EmployeeMainActivity
import com.example.assignease.R
import com.example.assignease.databinding.ActivitySplashBinding
import com.example.assignease.models.Users
import com.example.assignease.utils.utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
         val currentUser = FirebaseAuth.getInstance().currentUser?.uid

         if(currentUser != null) {
         lifecycleScope.launch {
             try {
                 FirebaseDatabase.getInstance().getReference("Users").child(currentUser).addListenerForSingleValueEvent(object  :ValueEventListener{
                     override fun onDataChange(snapshot: DataSnapshot) {

                         val currentUserdata = snapshot.getValue(Users::class.java)
                         when(currentUserdata?.usertype){
                             "Boss" -> {
                                 Log.d("TT" , "Bosslayout")
                                 val intent = Intent(this@SplashActivity, BossMainActivity::class.java)
                                 startActivity(intent)
                                 finish()
                             }
                             "Employee" -> {
                                 Log.d("TT" , "EmployeeLayout")
                                 val intent = Intent(this@SplashActivity, EmployeeMainActivity::class.java)
                                 startActivity(intent)
                                 finish()
                             }
                             else -> {
                                 val intent = Intent(this@SplashActivity,SignUp::class.java)
                                 startActivity(intent)
                                 finish()
                             }
                         }
                     }
                     override fun onCancelled(error: DatabaseError) {
                              utils.hidedialog()
                            utils.toastmessage(this@SplashActivity , error.message)
                     }

                 })
             }
             catch (e : Exception){
                   utils.toastmessage(this@SplashActivity, e.message!!)
             }
           }
         }
         else{
             val intent = Intent(this,SignUp::class.java)
             startActivity(intent)
             finish()
         }
        },3000)
    }
}