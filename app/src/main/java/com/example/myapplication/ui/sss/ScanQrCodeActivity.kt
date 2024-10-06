package com.example.myapplication.ui.sss

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.myapplication.R
import com.example.myapplication.data.network.RetrofitInstance
import com.example.myapplication.databinding.ActivityScanQrCodeBinding
import com.example.myapplication.databinding.LayoutAlertMessageSheetBinding
import com.example.myapplication.interfaces.ApiCallback
import com.example.myapplication.model.ScanResponse
import com.example.myapplication.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanQrCodeActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private lateinit var binding: ActivityScanQrCodeBinding
    private var isScanningEnabled = true // Flag to control scanning

    companion object {
        private const val MY_CAMERA_REQUEST_CODE = 6515
        fun getScanQrCodeActivity(callingClassContext: Context) =
            Intent(callingClassContext, ScanQrCodeActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan_qr_code)
        setScannerProperties()
    }

    private fun setScannerProperties() {
        binding.qrCodeScanner.setFormats(listOf(BarcodeFormat.QR_CODE))
        binding.qrCodeScanner.setAutoFocus(true)
    }

    override fun onResume() {
        super.onResume()
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                MY_CAMERA_REQUEST_CODE
            )
            return
        }
        binding.qrCodeScanner.startCamera()
        binding.qrCodeScanner.setResultHandler(this)
    }

    override fun onPause() {
        super.onPause()
        binding.qrCodeScanner.stopCamera()
    }

    override fun handleResult(result: Result?) {
        if (result != null && isScanningEnabled) {
            isScanningEnabled = false
            vibrate()
            val scannedData = result.text ?: ""
            updateExcel(scannedData)
        }
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    private fun showLoader() {
        binding.progressIndicator.visibility = View.VISIBLE
        binding.progressIndicator.show()
    }

    private fun hideLoader() {
        binding.progressIndicator.visibility = View.GONE
        binding.progressIndicator.hide()
    }

    private fun updateExcel(scannedData: String) {
        showLoader()
        val currentTimeStamp = System.currentTimeMillis() / 1000
        val day = when (currentTimeStamp) {
            in Constants.DAY_1_START_TIME..Constants.DAY_1_END_TIME -> Constants.DAY_1
            in Constants.DAY_2_START_TIME..Constants.DAY_2_END_TIME -> Constants.DAY_2
            in Constants.DAY_3_START_TIME..Constants.DAY_3_END_TIME -> Constants.DAY_3
            in Constants.DAY_4_START_TIME..Constants.DAY_4_END_TIME -> Constants.DAY_4
            in Constants.DAY_5_START_TIME..Constants.DAY_5_END_TIME -> Constants.DAY_5
            else -> "day1"
        }


        if (day.isNullOrBlank()) {
                Toast.makeText(
                    this@ScanQrCodeActivity,
                    "Pyrexia 2024 kicks off on Oct 10, 4 PM! Get ready!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                setBottomSheetForEntry(scannedData, day) // Show the dialog
            }
    }

    private fun setBottomSheetForEntry(scannedData: String, day: String) {
        val dialog = BottomSheetDialog(this@ScanQrCodeActivity, R.style.MyBottomSheetDialogTheme)
        val dialogBinding: LayoutAlertMessageSheetBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.layout_alert_message_sheet,
            null,
            false
        )

        dialog.setContentView(dialogBinding.root)
        val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT

        val behavior = BottomSheetBehavior.from(bottomSheet as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isDraggable = false

        dialogBinding.btnGotIt.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog
            isScanningEnabled = true // Enable scanning again
            binding.qrCodeScanner.resumeCameraPreview(this) // Resume the camera preview
        }


        // Call API and handle response...
        callApi(scannedData, day, object : ApiCallback {
            override fun onSuccess(response: String) {
                dialogBinding.txtMsg.text = response
                hideLoader()
                dialog.show()
            }

            override fun onFailure(errorMessage: String) {
                dialogBinding.txtMsg.text = errorMessage
                hideLoader()
                dialog.show()
            }
        })
    }

    private fun callApi(scannedData: String, day: String, callback: ApiCallback) {
        val apiService = RetrofitInstance.apiService

        val json = JsonObject().apply {
            addProperty("action", "update")
            addProperty("registrationNumber", scannedData)
            addProperty("day", day)
            addProperty("status", "Present")
        }

        apiService.scan(json).enqueue(object : Callback<ScanResponse> {
            override fun onResponse(call: Call<ScanResponse>, response: Response<ScanResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.status.equals("success")) {
                        val responseBody = response.body()?.message ?: ""
                        Log.d("ScanQrCodeActivity", "Response: $responseBody")
                        callback.onSuccess(responseBody)
                    } else if (response.body()?.status.equals("error")) {
                        val responseBody = response.body()?.message ?: ""
                        Log.d("ScanQrCodeActivity", "Response: $responseBody")
                        callback.onSuccess("Error: $responseBody")
                    }
                } else {
                    Log.e("ScanQrCodeActivity", "Error: ${response.code()}")
                    callback.onFailure("Something went wrong. Please try again.")
                }
            }

            override fun onFailure(call: Call<ScanResponse>, t: Throwable) {
                Log.e("ScanQrCodeActivity", "Error: ${t.message}")
                callback.onFailure("Something went wrong. Please try again.")
            }
        })
    }
}