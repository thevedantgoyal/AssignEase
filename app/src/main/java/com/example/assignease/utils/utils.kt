package com.example.assignease.utils

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.Toast
import com.example.assignease.R
import com.example.assignease.adapters.EmployeesAdaptor
import com.google.android.material.snackbar.Snackbar

object utils {

    private var progressdialog : AlertDialog? = null

     fun showdialog(context : Context){
        progressdialog = AlertDialog.Builder(context).setView(R.layout.dialog).setCancelable(false).create()

        progressdialog!!.show()
    }

    fun hidedialog(){
        progressdialog?.dismiss()
    }

    fun toastmessage(context: Context, message: String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }

    fun snackbar(itemView : View){
        Snackbar.make(itemView,"Data is Cleared",Snackbar.LENGTH_SHORT).show()
    }
}