@file:Suppress("DEPRECATION")

package com.example.assignease.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.example.assignease.R
import com.example.assignease.databinding.ActivityOtpBinding
import com.example.assignease.utils.utils
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var binding : ActivityOtpBinding
    private lateinit var Otp : String
    private lateinit var resendToken :  PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber : String
    private lateinit var progressbar : ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOtpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        Otp = intent.getStringExtra("OTP").toString()
        if (intent != null && intent.hasExtra("resendToken")) {
            resendToken = intent.getParcelableExtra("resendToken")!!
        }
        phoneNumber = intent.getStringExtra("phoneNumber").toString()
        progressbar = binding.otpProgressBar

        progressbar.visibility = View.INVISIBLE
        addTextChangeListener()


        binding.resendTextView.setOnClickListener {
            if(resendToken != null){
                resendVerificationCode()
                resendOtpVisibility()
            }
            else{
                utils.toastmessage(this, "Cannot resend OTP at this moment. Please try again later.")
                Log.e("OtpActivity", "Resend token is null")
            }
        }

        binding.verifyOTPBtn.setOnClickListener {
            val typedOtp =( binding.otpEditText1.text.toString() + binding.otpEditText2.text.toString() + binding.otpEditText3.text.toString() + binding.otpEditText4.text.toString() + binding.otpEditText5.text.toString() + binding.otpEditText6.text.toString())
            if(typedOtp.isNotEmpty()){
                if(typedOtp.length == 6){

                    val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(Otp , typedOtp)

                    progressbar.visibility = View.VISIBLE
                    signInwithPhoneAuthentication(credential)

                }else{
                    utils.toastmessage(this , "Please Enter Correct Otp!!")
                }
            }else{
                utils.toastmessage(this , "Please Enter Otp!!")
            }
        }
    }

    private fun resendOtpVisibility(){
        binding.otpEditText1.setText("")
        binding.otpEditText2.setText("")
        binding.otpEditText3.setText("")
        binding.otpEditText4.setText("")
        binding.otpEditText5.setText("")
        binding.otpEditText6.setText("")

        binding.resendTextView.visibility = View.INVISIBLE
        binding.resendTextView.isEnabled = false

        Handler(Looper.myLooper()!!).postDelayed(Runnable {
          binding.resendTextView.visibility = View.VISIBLE
            binding.resendTextView.isEnabled = true
        },60000)

    }
    private fun resendVerificationCode(){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setForceResendingToken(resendToken)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInwithPhoneAuthentication(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    utils.toastmessage(this , "Authenticate Successfully")
                    sendToMain()

                } else {

                    Log.d("phone", "signInWithCredential:failure : ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                    }
                }
                utils.showdialog(this)
            }

    }
    private fun sendToMain(){
        val intent = Intent(this , SignIn::class.java)
        startActivity(intent)
        finish()
    }


    private fun addTextChangeListener(){
       binding.otpEditText1.addTextChangedListener(editTextWatcher(binding.otpEditText1))
        binding.otpEditText2.addTextChangedListener(editTextWatcher(binding.otpEditText2))
        binding.otpEditText3.addTextChangedListener(editTextWatcher(binding.otpEditText3))
        binding.otpEditText4.addTextChangedListener(editTextWatcher(binding.otpEditText4))
        binding.otpEditText5.addTextChangedListener(editTextWatcher(binding.otpEditText5))
        binding.otpEditText6.addTextChangedListener(editTextWatcher(binding.otpEditText6))
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInwithPhoneAuthentication(credential)
        }

        override fun onVerificationFailed(e : FirebaseException) {

            if(e is FirebaseAuthInvalidCredentialsException){
                Log.d("phone" , "onverification failed : ${e.toString()}")
                utils.toastmessage(this@OtpActivity , "Phone Verifivation Failed")
            }
            else if(e is FirebaseTooManyRequestsException){
                Log.d("phone" , "onverification status : ${e.toString()}")
                utils.toastmessage(this@OtpActivity , "Verifivation status Failed")
            }
            utils.showdialog(this@OtpActivity)
        }

        override fun onCodeSent(verificationId: String, token : PhoneAuthProvider.ForceResendingToken) {
            Otp = verificationId
            resendToken = token
        }
    }

    inner class editTextWatcher(private val view: View ) : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            when(view.id){
                R.id.otpEditText1 -> if(text.length == 1)   binding.otpEditText2.requestFocus()
                R.id.otpEditText2 -> if(text.length == 1)   binding.otpEditText3.requestFocus() else if(text.isEmpty()) binding.otpEditText1.requestFocus()
                R.id.otpEditText3 -> if(text.length == 1)   binding.otpEditText4.requestFocus() else if(text.isEmpty()) binding.otpEditText2.requestFocus()
                R.id.otpEditText4 -> if(text.length == 1)   binding.otpEditText5.requestFocus() else if(text.isEmpty()) binding.otpEditText3.requestFocus()
                R.id.otpEditText5 -> if(text.length == 1)   binding.otpEditText6.requestFocus() else if(text.isEmpty()) binding.otpEditText4.requestFocus()
                R.id.otpEditText6 -> if(text.isEmpty()) binding.otpEditText5.requestFocus()

            }
        }

    }
}