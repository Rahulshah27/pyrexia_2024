package com.example.myapplication.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Environment
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


fun generateQRCode(data: String): Bitmap {
    val size = 512 // Define the size of the QR code
    val bitMatrix: BitMatrix =
        MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, size, size)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
        }
    }
    return bitmap
}

fun addTextBelowQRCode(
    context: Context,
    qrCodeBitmap: Bitmap,
    registrationNumber: String
): Bitmap {
    val padding = (10 * context.resources.displayMetrics.density).toInt()
    val spaceBetweenQrAndText = (20 * context.resources.displayMetrics.density).toInt()
    val spaceBelowText = (30 * context.resources.displayMetrics.density).toInt()

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }

    val textBounds = Rect()
    paint.getTextBounds(registrationNumber, 0, registrationNumber.length, textBounds)

    val combinedHeight =
        qrCodeBitmap.height + padding + textBounds.height() + spaceBetweenQrAndText + spaceBelowText
    val resultBitmap =
        Bitmap.createBitmap(qrCodeBitmap.width, combinedHeight, Bitmap.Config.RGB_565)

    val canvas = Canvas(resultBitmap)
    canvas.drawColor(Color.WHITE)

    canvas.drawBitmap(qrCodeBitmap, 0f, 0f, null)

    val textYPosition = qrCodeBitmap.height + padding + spaceBetweenQrAndText

    canvas.drawText(
        registrationNumber,
        (qrCodeBitmap.width / 2).toFloat(),
        textYPosition.toFloat(),
        paint
    )
    return resultBitmap
}

fun saveQRCodeToPyrexiaFolder(bitmap: Bitmap, registrationNumber: String) {
    val downloadsPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val pyrexiaFolder = File(downloadsPath, "Pyrexia_2024")

    // Check if the directory exists, if not, create it
    if (!pyrexiaFolder.exists()) {
        val wasDirectoryCreated =
            pyrexiaFolder.mkdirs() // Create the directory if it doesn't exist
        if (!wasDirectoryCreated) {
            Log.e("QRCodeWorker", "Failed to create directory: ${pyrexiaFolder.absolutePath}")
            return
        }
    }

    // Create a file for the QR code
    val file = File(pyrexiaFolder, "$registrationNumber-QRCode.png")

    var outputStream: OutputStream? = null
    try {
        outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        Log.d("QRCodeWorker", "QR code saved to: ${file.absolutePath}")
    } catch (e: Exception) {
        Log.e("QRCodeWorker", "Error saving QR code", e)
    } finally {
        outputStream?.close()
    }
}


