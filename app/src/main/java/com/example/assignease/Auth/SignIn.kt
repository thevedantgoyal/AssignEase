package com.example.assignease.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.assignease.BossMainActivity
import com.example.assignease.EmployeeMainActivity
import com.example.assignease.R
import com.example.assignease.databinding.ActivitySignInBinding
import com.example.assignease.databinding.ForgotPasswordBinding
import com.example.assignease.models.Users
import com.example.assignease.utils.utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignIn : AppCompatActivity() {
    private lateinit var binding : ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            loginEmail(email , password)
        }
        binding.signinSignupbtn.setOnClickListener {
            val intent = Intent(this , SignUp::class.java)
            startActivity(intent)
            finish()
        }

        binding.loginForgot.setOnClickListener { forgotPassword() }
    }

    private fun forgotPassword() {
        val dialog = ForgotPasswordBinding.inflate(LayoutInflater.from(this))
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialog.root)
            .create()
        alertDialog.show()

        dialog.forgotEmail.requestFocus()
        dialog.forgotSignin.setOnClickListener {
            val intent = Intent(this , SignIn::class.java)
            startActivity(intent)
            finish()
        }

        dialog.forgotpassBtn.setOnClickListener {
            val email = dialog.forgotEmail.text.toString()
            alertDialog.dismiss()
            resetPassword(email)
        }
    }

    private fun resetPassword(email: String) {
        lifecycleScope.launch {
            try {
               FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
                utils.toastmessage(this@SignIn,"Please Check your email and Reset the Password!!")
            }
            catch (e : Exception){
                utils.toastmessage(this@SignIn,"Error while Resetting : ${e.message.toString()}")
            }
        }
    }

    private fun loginEmail(email: String, password: String) {
        utils.showdialog(this)
        val firebaseAuth = FirebaseAuth.getInstance()
        lifecycleScope.launch {
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email,password).await()
                val currentUser = authResult.user?.uid

                val verifyemail = FirebaseAuth.getInstance().currentUser?.isEmailVerified
                if(verifyemail == true){

                    if(currentUser != null){
                        FirebaseDatabase.getInstance().getReference("Users").child(currentUser).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val currentUserdata = snapshot.getValue(Users::class.java)
                                when(currentUserdata?.usertype){
                                    "Boss" -> {
                                        Log.d("TT" , "Bosslayout")
                                        utils.hidedialog()
                                        val intent = Intent(this@SignIn,BossMainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    "Employee" -> {
                                        utils.hidedialog()
                                        val intent = Intent(this@SignIn,EmployeeMainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else -> {
                                        utils.hidedialog()
                                        utils.toastmessage(this@SignIn,"User is Not defined Please sign up Again!!")
                                        val intent = Intent(this@SignIn,SignUp::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }

                            }

                            override fun onCancelled(error: DatabaseError) {
                                utils.hidedialog()
                                utils.toastmessage(this@SignIn,error.message)
                            }
                        })

                    }else{
                        utils.hidedialog()
                        utils.toastmessage(this@SignIn , "User Not found, Please SignUp First!!")
                    }
                }
                else{
                    utils.hidedialog()
                    utils.toastmessage(this@SignIn,"Email is Not Verified!!")
                }
            }
            catch (e : Exception){
               utils.hidedialog()
                utils.toastmessage(this@SignIn , "Upload Failed : ${e.message}")
            }
        }
    }
}