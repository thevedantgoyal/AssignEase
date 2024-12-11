package com.example.assignease

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.assignease.Auth.SignIn
import com.example.assignease.databinding.ActivityEmployeeMainBinding
import com.google.firebase.auth.FirebaseAuth

class EmployeeMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_main)
    }
}