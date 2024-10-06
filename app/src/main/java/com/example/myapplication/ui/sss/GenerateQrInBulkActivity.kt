package com.example.myapplication.ui.sss

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.R
import com.example.myapplication.data.network.RetrofitInstance
import com.example.myapplication.databinding.ActivityGenerateQrCodeBulkBinding
import com.example.myapplication.interfaces.ApiCallback
import com.example.myapplication.model.AddInBulkResponse
import com.example.myapplication.model.RowData
import com.example.myapplication.utils.Constants.STORAGE_PERMISSION_CODE
import com.example.myapplication.utils.QRCodeWorker
import com.example.myapplication.utils.saveRegistrationNumbersToFile
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.WorkbookFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream

class GenerateQrInBulkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGenerateQrCodeBulkBinding

    companion object {
        fun getGenerateInBulkActivity(callingClassContext: Context) =
            Intent(callingClassContext, GenerateQrInBulkActivity::class.java)
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    readExcelFile(uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_generate_qr_code_bulk)

        askPermission()
        initClick()
    }

    private fun askPermission() {
        checkStoragePermission()
    }

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE)
        } else {
            initClick()
        }
    }

    private fun initClick() {
        binding.btnSelectFile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
                startForResult.launch(intent)
            } else {
                Toast.makeText(this, "Storage permission is required to select a file", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun readExcelFile(uri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val jsonData = parseExcel(this, inputStream)
                Toast.makeText(this, "Excel data parsed to JSON: $jsonData", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseExcel(context: Context, inputStream: InputStream): String {
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)  // Get first sheet
        val rows = mutableListOf<RowData>()
        val registrationNumbers = mutableListOf<String>()
        val dataFormatter = DataFormatter()
        var isValidHeader = false

        for (row in sheet) {
            if (row.rowNum == 0) {
                val header = row.getCell(0)?.toString()?.trim()?.lowercase() ?: ""

                if (header == "registration_number") {
                    isValidHeader = true
                    continue
                } else {
                    Toast.makeText(
                        context,
                        "ExcelParsing, invalid column headers. Please check your file.",
                        Toast.LENGTH_SHORT
                    ).show()
                    workbook.close()
                    return ""
                }
            }

            if (isValidHeader) {
                val registrationNumber = dataFormatter.formatCellValue(row.getCell(0)).trim()

                // Restrict empty registration numbers
                if (registrationNumber.isNotBlank()) {
                    rows.add(RowData(registrationNumber))
                    registrationNumbers.add(registrationNumber)
                }
            }
        }

        workbook.close()

        if (registrationNumbers.isEmpty()) {
            Toast.makeText(context, "No valid registration numbers found.", Toast.LENGTH_SHORT)
                .show()
            return ""
        }

        val filePath = saveRegistrationNumbersToFile(context, registrationNumbers)

        val inputData = Data.Builder()
            .putString("registration_numbers_file", filePath)
            .build()

        val qrCodeWorkRequest = OneTimeWorkRequestBuilder<QRCodeWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(qrCodeWorkRequest)

        val gson = Gson()
        val rowsJsonArray = gson.toJsonTree(rows).asJsonArray
        val jsonObject = JsonObject().apply {
            addProperty("action", "bulk")
            add("entries", rowsJsonArray)
        }

        callApiWithJson(jsonObject, object : ApiCallback {
            override fun onSuccess(response: String) {
            }

            override fun onFailure(errorMessage: String) {

            }
        })

        return jsonObject.toString()
    }


    private fun callApiWithJson(jsonObject: JsonObject, callback: ApiCallback) {
        val apiService = RetrofitInstance.apiService
        apiService.addInBulk(jsonObject).enqueue(object : Callback<AddInBulkResponse> {
            override fun onResponse(
                call: Call<AddInBulkResponse>,
                response: Response<AddInBulkResponse>
            ) {
                if (response.isSuccessful) {
                    val res = response.body()
                    if (res?.status.equals("success") && res?.existingRegistrationNumbers.isNullOrEmpty()) {
                        callback.onSuccess(res?.message ?: "")
                    } else if (res?.status.equals("success") && res?.existingRegistrationNumbers?.isNotEmpty() == true) {
                        var existingRegs = ""
                        res.existingRegistrationNumbers.forEach { er->
                            existingRegs += "$er,"
                        }
                        callback.onSuccess("${res.message ?: ""} and $existingRegs")
                    }
                    else if (response.body()?.status.equals("error")) {
                        val responseBody = response.body()?.message ?: ""
                        callback.onSuccess(responseBody)
                    }
                } else {
                    callback.onFailure("Something went wrong. Please try again.")
                }
            }

            override fun onFailure(call: Call<AddInBulkResponse>, t: Throwable) {
                callback.onFailure("Something went wrong. Please try again.")
            }
        })
    }
}