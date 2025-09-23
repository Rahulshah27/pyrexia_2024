package com.example.myapplication.ui.sss

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityFirstBinding



class FirstActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirstBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_first)

        binding.btnGenerateSingleQr.setOnClickListener {
            startActivity(GenerateQrCodeActivity.getGenerateQrCodeActivity(this))
        }

        binding.btnGenerateBulkQr.setOnClickListener {
            startActivity(GenerateQrInBulkActivity.getGenerateInBulkActivity(this))
        }

        binding.firstActivityScanButton.setOnClickListener {
            startActivity(ScanQrCodeActivity.getScanQrCodeActivity(this))
        }

        binding.btnGoToAdmin.setOnClickListener {
            startActivity(Intent( this@FirstActivity, AdminActivity::class.java))
        }
    }

}