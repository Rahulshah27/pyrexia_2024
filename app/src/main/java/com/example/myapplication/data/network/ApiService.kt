package com.example.myapplication.data.network

import com.example.myapplication.model.AddInBulkResponse
import com.example.myapplication.model.AddResponse
import com.example.myapplication.model.ScanResponse
import com.example.myapplication.utils.Constants.ENDPOINT_TEST
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST(ENDPOINT_TEST)
    fun addInBulk(@Body json: JsonObject): Call<AddInBulkResponse>

    @POST(ENDPOINT_TEST)
    fun add(@Body json: JsonObject): Call<AddResponse>

    @POST(ENDPOINT_TEST)
    fun scan(@Body json: JsonObject): Call<ScanResponse>

    @POST(ENDPOINT_TEST)
    fun updateActiveStatus(@Body json: JsonObject): Call<ScanResponse>
}