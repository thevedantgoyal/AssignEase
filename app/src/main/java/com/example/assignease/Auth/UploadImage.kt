package com.example.assignease.Auth

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.assignease.databinding.ActivityUploadImageBinding
import com.example.assignease.utils.utils
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class UploadImage : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var number : String
    private lateinit var binding: ActivityUploadImageBinding
    private lateinit var storeverificationId : String
    private lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    private var userImageUri : Uri? = null
    private var selectImage = registerForActivityResult(ActivityResultContracts.GetContent()){
        userImageUri = it
        binding.signupProfileImage.setImageURI(userImageUri)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupUplaodTxt.setOnClickListener {
            selectImage.launch("image/*")
        }

        auth = FirebaseAuth.getInstance()
        binding.uploadNewbtn.setOnClickListener {
            utils.showdialog(this)

            number = binding.phoneEditTextNumber.text.trim().toString()
            if (number.isNotEmpty()){
                if(number.length == 10){
                    number = "+91${number}"
                    utils.showdialog(this)
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number)
                        .setTimeout(60L,TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks)
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)

                }
                else{
                    utils.toastmessage(this , "Please Enter Correct Number!")
                }
            }else{
                utils.toastmessage(this , "Please Enter the Number!")

            }
            val currentUserid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val storageReference =
                FirebaseStorage.getInstance().getReference("Profile").child(currentUserid)
                    .child("Profile.jpg")

            lifecycleScope.launch {

                try {
                    val uploadtask = storageReference.putFile(userImageUri!!).await()
                    if (uploadtask.task.isSuccessful) {
                        val downloadUrl = storageReference.downloadUrl.await()

                        updatedata(currentUserid,downloadUrl,number)
                    } else {
                        utils.hidedialog()
                        showToast("Upload Failed -: ${uploadtask.task.exception?.message}")
                    }
                } catch (e: Exception) {
                    utils.hidedialog()
                    showToast("upload failed exception -:  ${e.message}")
                }
            }
        }
    }

    private fun updatedata(currentUserid: String, downloadUrl: Uri?, number: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserid)
        val updates = hashMapOf<String , Any>(
            "userImage" to downloadUrl.toString(),
            "userPhoneNo" to number
        )

        databaseReference.updateChildren(updates).addOnSuccessListener {
            utils.toastmessage(this,"Successful uploaded")

            utils.hidedialog()
        }
            .addOnFailureListener {
                utils.toastmessage(this,"not successful uploaded")
            }
    }

    private fun showToast(message : String) {
        runOnUiThread {
            Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInwithPhoneAuthentication(credential)
        }

        override fun onVerificationFailed(e : FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.


            if(e is FirebaseAuthInvalidCredentialsException){
                Log.d("phone" , "onverification failed : ${e.toString()}")
                utils.toastmessage(this@UploadImage , "Phone Verifivation Failed")
                utils.hidedialog()
            }
            else if(e is FirebaseTooManyRequestsException){
                Log.d("phone" , "onverification status : ${e.toString()}")
                utils.toastmessage(this@UploadImage , "Verifivation status Failed")
                utils.hidedialog()
            }
            utils.showdialog(this@UploadImage)
        }

        override fun onCodeSent(verificationId: String, token : PhoneAuthProvider.ForceResendingToken) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later


            storeverificationId = verificationId
            resendToken = token

            val intent = Intent(this@UploadImage , OtpActivity::class.java)
            intent.putExtra("OTP" , storeverificationId)
            intent.putExtra("resendToken " , resendToken.toString())
            intent.putExtra("phoneNumber" , number)
            startActivity(intent)
            finish()
            utils.hidedialog()
        }
    }

    private fun signInwithPhoneAuthentication(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    utils.toastmessage(this , "Authenticate Successfully")

                    val intent = Intent(this@UploadImage , SignIn::class.java)
                    startActivity(intent)

                    utils.hidedialog()

                } else {

                    Log.d("phone", "signInWithCredential:failure : ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                    }
                }
                utils.hidedialog()
            }
    }


}