package com.example.myapplication.interfaces

interface ApiCallback {
    fun onSuccess(response: String)
    fun onFailure(errorMessage: String)
}