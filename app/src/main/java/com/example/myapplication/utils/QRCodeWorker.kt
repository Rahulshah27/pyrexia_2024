package com.example.myapplication.utils

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File

class QRCodeWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        // Retrieve the file path from inputData
        val filePath = inputData.getString("registration_numbers_file") ?: return Result.failure()

        // Read registration numbers from the file
        val registrationNumberList = File(filePath).readLines()

        // Loop through the registration numbers and generate/save QR codes
        for (registrationNumber in registrationNumberList) {
            if (registrationNumber.isBlank()) {  // Skip empty registration numbers
                Log.e("QRCodeWorker", "Empty registration number found, skipping...")
                continue  // Skip to next registration number
            }

            try {
                // Generate QR code for each registration number
                val qrCodeBitmap = generateQRCode(registrationNumber)

                // Add registration number text below the QR code
                val qrWithTextBitmap = addTextBelowQRCode(applicationContext, qrCodeBitmap, registrationNumber)

                // Save the QR code to the "Pyrexia_2024" folder in internal storage
                saveQRCodeToPyrexiaFolder(qrWithTextBitmap, registrationNumber)

            } catch (e: Exception) {
                Log.e("QRCodeWorker", "Error generating/saving QR code for $registrationNumber", e)
            }
        }

        return Result.success()
    }
}