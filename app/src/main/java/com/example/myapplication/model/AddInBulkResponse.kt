package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class AddInBulkResponse(
    @SerializedName("status")
    val status: String?=null,
    @SerializedName("message")
    val message: String?=null,
    @SerializedName("existingRegistrationNumbers")
    val existingRegistrationNumbers: List<String>?=null
)