package com.example.myapplication.utils

import android.content.Context
import java.io.File

fun saveRegistrationNumbersToFile(context: Context, registrationNumbers: List<String>): String {
    val file = File(context.filesDir, "registration_numbers.txt")
    file.writeText(registrationNumbers.joinToString("\n"))
    return file.absolutePath
}