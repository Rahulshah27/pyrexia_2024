package com.example.myapplication.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.constants.Constants

class AppPreferences(var context: Context) {

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);


    fun getNeedToClear() = sharedPreferences.getBoolean(Constants.NEED_TO_CLEAR, true)

}