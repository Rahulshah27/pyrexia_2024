package com.example.myapplication.data.network

import com.example.myapplication.model.AddInBulkResponse
import com.example.myapplication.model.AddResponse
import com.example.myapplication.model.ScanResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("macros/s/AKfycbx_UEUL8L0em5Qs8mnuKXQOUNLbxdMOHCyTgrlj6MQv8rM9gaBpJ1Cm7tDpnl27zFSz/exec")
    fun addInBulk(@Body json: JsonObject): Call<AddInBulkResponse>

    @POST("macros/s/AKfycbx_UEUL8L0em5Qs8mnuKXQOUNLbxdMOHCyTgrlj6MQv8rM9gaBpJ1Cm7tDpnl27zFSz/exec")
    fun add(@Body json: JsonObject): Call<AddResponse>

    @POST("macros/s/AKfycbx_UEUL8L0em5Qs8mnuKXQOUNLbxdMOHCyTgrlj6MQv8rM9gaBpJ1Cm7tDpnl27zFSz/exec")
    fun scan(@Body json: JsonObject): Call<ScanResponse>
}