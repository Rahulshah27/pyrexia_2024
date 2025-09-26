package com.example.myapplication.model

import com.google.gson.annotations.SerializedName


data class ScanResponse(
    @SerializedName("success")
    val success: Boolean?=null,
    @SerializedName("updated")
    val updated: Int?=null,
    @SerializedName("message")
    val message: String?=null
)
