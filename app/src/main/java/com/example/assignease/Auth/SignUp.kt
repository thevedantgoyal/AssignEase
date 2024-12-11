package com.example.assignease.Auth

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.assignease.databinding.ActivitySignUpBinding
import com.example.assignease.databinding.EmailVerifyBinding
import com.example.assignease.models.Users
import com.example.assignease.utils.utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
//    private var userImageUri : Uri? = null
    private var userType : String? = ""
//    private var selectImage = registerForActivityResult(ActivityResultContracts.GetContent()){
//       userImageUri = it
//        binding.signupProfileImage.setImageURI(userImageUri)
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            binding.Radiogroup.setOnCheckedChangeListener { _, checkedId ->
                userType = findViewById<RadioButton>(checkedId).text.toString()
                Log.d("TT" , userType.toString())
            }

            binding.signupBtn.setOnClickListener { createUser() }
        }

    binding.signupSigninbtn.setOnClickListener {
        val intent = Intent(this , SignIn::class.java)
        startActivity(intent)
        finish()
    }
    }

    private fun createUser() {
        utils.showdialog(this)

        val name = binding.signupName.text.toString()
        val email = binding.signupEmail.text.toString()
        val password = binding.signupPassword.text.toString()
        val confPass = binding.signupConfPassword.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confPass.isNotEmpty()){
            if(password == confPass){
                if(userType != ""){
                    savedUserData(name,email,password)
                }
                else{
                    utils.hidedialog()
                    utils.toastmessage(this,"Please Select usertype")
                }
            }
            else{
                utils.toastmessage(this,"Password are not matching")
            }
        }
        else{
            utils.hidedialog()
            utils.toastmessage(this,"Please enter all the Fields")
        }
    }

    private fun savedUserData(name: String, email: String, password: String) {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) return@OnCompleteListener

            val token = task.result
            lifecycleScope.launch {

                val database = FirebaseDatabase.getInstance().getReference("Users")
                try {

                    val firebaseAuth = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).await()

                    if(firebaseAuth.user != null){

                        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()?.addOnSuccessListener {
                            val dialog = EmailVerifyBinding.inflate(LayoutInflater.from(this@SignUp))
                            val alertdialog = AlertDialog.Builder(this@SignUp)
                                .setView(dialog.root)
                                .create()
                            utils.hidedialog()
                            alertdialog.show()
                            dialog.accountBtn.setOnClickListener {
                                alertdialog.dismiss()
                                val intent = Intent(this@SignUp , UploadImage::class.java)
                                startActivity(intent)
                                finish()
                                utils.toastmessage(this@SignUp,"SignUp Successfully!")
                            }
                        }
                        val uId = firebaseAuth.user?.uid.toString()
                        val savedUsertype = if(userType == "Boss") "Boss" else "Employee"


                        val user = Users(userId = uId, userName = name, userEmail = email, userPassword = password , usertype = savedUsertype, userToken = token)
                        database.child(uId).setValue(user).await()

                    }
                    else{
                        utils.hidedialog()
                        utils.toastmessage(this@SignUp,"SignUp Failed !! ")
                    }

                }
                catch (e : Exception){
                    utils.hidedialog()
                    utils.toastmessage(this@SignUp,e.message.toString())

                }
            }
        })
    }
    private fun showToast(message : String) {
        runOnUiThread {
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        }
    }

}