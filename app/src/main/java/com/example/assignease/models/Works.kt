package com.example.assignease.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID


data class Works(
    val id : String? = UUID.randomUUID().toString(),
    val workId : String? = null,
    val workTitle :String? = null,
    val workDescription : String? = null,
    val workPriority : String? = null,
    val bossId : String? = null,
    val workLastDate : String?= null,
    val workStatus : String? = null,
    var expanded : Boolean = false,
    var hasAnyWork : Boolean = false
)
