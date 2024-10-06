package com.example.myapplication.ui.sss

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.myapplication.R
import com.example.myapplication.data.network.RetrofitInstance
import com.example.myapplication.databinding.ActivityGenerateQrCodeBinding
import com.example.myapplication.interfaces.ApiCallback
import com.example.myapplication.model.AddResponse
import com.example.myapplication.utils.addTextBelowQRCode
import com.example.myapplication.utils.generateQRCode
import com.example.myapplication.utils.saveQRCodeToPyrexiaFolder
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GenerateQrCodeActivity : AppCompatActivity() {
    lateinit var binding: ActivityGenerateQrCodeBinding

    companion object {
        fun getGenerateQrCodeActivity(callingClassContext: Context) =
            Intent(callingClassContext, GenerateQrCodeActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_generate_qr_code)
        initClicks()
    }

    private fun initClicks() {
        // Generate QR Code button
        binding.generateQrCodeButton.setOnClickListener {
            if (checkEditText()) {
                hideKeyboard()
                generateQrCodeAndSave()
            }
        }

    }

    private fun generateQrCodeAndSave() {
        val registrationNumber = binding.etRegNo.text.toString()
        try {
            if (registrationNumber.isBlank()) {
                Toast.makeText(
                    this@GenerateQrCodeActivity,
                    "please enter valid registration number",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val qrCodeBitmap = generateQRCode(registrationNumber)

                val qrWithTextBitmap =
                    addTextBelowQRCode(applicationContext, qrCodeBitmap, registrationNumber)

                saveQRCodeToPyrexiaFolder(qrWithTextBitmap, registrationNumber)

                binding.qrCodeImageView.setImageBitmap(qrWithTextBitmap)

                callApi(registrationNumber, object : ApiCallback {
                    override fun onSuccess(response: String) {
                        Toast.makeText(this@GenerateQrCodeActivity, response, Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onFailure(errorMessage: String) {
                        Toast.makeText(
                            this@GenerateQrCodeActivity,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

        } catch (e: Exception) {
            Log.e("QRCodeWorker", "Error generating/saving QR code for $registrationNumber", e)
        }
    }

    private fun callApi(registrationNumber: String, callback: ApiCallback) {
        val apiService = RetrofitInstance.apiService
        val jsonObject = JsonObject()
        jsonObject.addProperty("action", "add")
        jsonObject.addProperty("registrationNumber", registrationNumber)
        jsonObject.addProperty("day1", "Absent")
        jsonObject.addProperty("day2", "Absent")
        jsonObject.addProperty("day3", "Absent")
        jsonObject.addProperty("day4", "Absent")
        jsonObject.addProperty("day5", "Absent")

        apiService.add(jsonObject).enqueue(object : Callback<AddResponse> {
            override fun onResponse(
                call: Call<AddResponse>,
                response: Response<AddResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.status.equals("success")) {
                        val responseBody = response.body()?.message ?: ""
                        callback.onSuccess(responseBody)
                    }else if (response.body()?.status.equals("error")) {
                        val responseBody = response.body()?.message ?: ""
                        callback.onSuccess(responseBody)
                    }
                } else {
                    callback.onFailure("Something went wrong. Please try again.")
                }
            }

            override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                callback.onFailure("Something went wrong. Please try again.")
            }
        })
    }


    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun checkEditText(): Boolean {
        when {
            TextUtils.isEmpty(binding.etRegNo.text.toString()) -> {
                Toast.makeText(
                    this,
                    "Registration Number field cannot be empty!",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }

            else -> return true
        }
    }


}