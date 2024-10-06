package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class AddResponse(
    @SerializedName("status")
    val status: String?=null,
    @SerializedName("message")
    val message: String?=null
)

