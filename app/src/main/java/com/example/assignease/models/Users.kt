package com.example.assignease.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
data class Users(
    val id : String = UUID.randomUUID().toString(),
    val userId : String? = null,
    val userName : String? = null,
    val userEmail : String? = null,
    val userPassword : String? = null,
    val userImage : String? = null,
    val usertype : String? = null,
    val userPhoneNo :String?=null,
    val userToken : String? = null,


):Parcelable
