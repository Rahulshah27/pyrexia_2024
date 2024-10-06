package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class ScanResponse(
    @SerializedName("message")
    val message:String?=null,
    @SerializedName("status")
    val status:String?=null
)
