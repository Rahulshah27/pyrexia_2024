package com.example.myapplication.data.network

import com.example.myapplication.model.AddInBulkResponse
import com.example.myapplication.model.AddResponse
import com.example.myapplication.model.ScanResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("macros/s/AKfycbyzooe_evzEdcKWrNlVaMdl9C8o760635fcpLdkcH6A-KS8CjdVPct6vhYby8g_MWpa/exec")
    fun addInBulk(@Body json: JsonObject): Call<AddInBulkResponse>

    @POST("macros/s/AKfycbyzooe_evzEdcKWrNlVaMdl9C8o760635fcpLdkcH6A-KS8CjdVPct6vhYby8g_MWpa/exec")
    fun add(@Body json: JsonObject): Call<AddResponse>

    @POST("macros/s/AKfycbyzooe_evzEdcKWrNlVaMdl9C8o760635fcpLdkcH6A-KS8CjdVPct6vhYby8g_MWpa/exec")
    fun scan(@Body json: JsonObject): Call<ScanResponse>
}