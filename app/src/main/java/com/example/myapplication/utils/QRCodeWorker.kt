package com.example.myapplication.utils

import android.content.Context
import android.graphics.*
import android.os.Environment
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class QRCodeWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val filePath = inputData.getString("registration_numbers_file") ?: return Result.failure()

        val registrationNumberList = File(filePath).readLines()

        for (registrationNumber in registrationNumberList) {
            if (registrationNumber.isBlank()) {
                Log.e("QRCodeWorker", "Empty registration number found, skipping...")
                continue
            }

            try {
                val qrCodeBitmap = generateQRCode(registrationNumber)

                val qrWithTextBitmap = addTextBelowQRCode(applicationContext, qrCodeBitmap, registrationNumber)

                saveQRCodeToPyrexiaFolder(qrWithTextBitmap, registrationNumber)

            } catch (e: Exception) {
                Log.e("QRCodeWorker", "Error generating/saving QR code for $registrationNumber", e)
            }
        }

        return Result.success()
    }
}