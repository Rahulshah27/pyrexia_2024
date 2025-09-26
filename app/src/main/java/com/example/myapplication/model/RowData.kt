package com.example.myapplication.model


data class RowData(
    val registration_number: String,
    val day1: String = "Absent",
    val day2: String = "Absent",
    val day3: String = "Absent",
    val day4: String = "Absent",
    val day5: String = "Absent",
    val active: String = "No"
)
