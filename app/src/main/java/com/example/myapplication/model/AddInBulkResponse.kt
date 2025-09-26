package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class AddInBulkResponse(
    @SerializedName("status")
    val status: String?=null,
    @SerializedName("added")
    val added: Int?=null,
    @SerializedName("message")
    val message: String?=null,
    @SerializedName("skipped")
    val existingRegistrationNumbers: List<String>?=null
)