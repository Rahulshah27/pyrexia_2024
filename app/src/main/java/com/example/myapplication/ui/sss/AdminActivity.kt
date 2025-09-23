package com.example.myapplication.ui.sss

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import androidx.core.content.edit
import com.example.myapplication.utils.Constants.IS_ADMIN_DATA
import com.example.myapplication.utils.Constants.KEY_ADMIN_EMAIL
import com.example.myapplication.utils.Constants.KEY_ADMIN_PASSWORD

// change to your package

class AdminActivity : AppCompatActivity() {
    private var etEmail: TextInputEditText? = null
    private var etPassword: TextInputEditText? = null
    private var btnSignIn: MaterialButton? = null
    private var progressBar: ProgressBar? = null

    private var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        // If already logged in, skip login
        if (sharedPreferences?.getBoolean(KEY_LOGGED_IN, false) == true) {
            startActivity(Intent(this, ScanQrCodeActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_admin)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignIn = findViewById(R.id.btnSignIn)
        progressBar = findViewById(R.id.progress)

        btnSignIn?.setOnClickListener { v: View? -> loginUser() }
    }

    private fun loginUser() {
        val email = if (etEmail?.getText() != null) etEmail?.getText().toString()
            .trim { it <= ' ' } else ""
        val password = if (etPassword?.getText() != null) etPassword?.getText().toString()
            .trim { it <= ' ' } else ""

        if (email.isEmpty()) {
            etEmail?.error = "Enter email"
            etEmail?.requestFocus()
            return
        }
        if (password.isEmpty()) {
            etPassword?.error = "Enter password"
            etPassword?.requestFocus()
            return
        }

        // Show progress
        progressBar?.visibility = View.VISIBLE
        btnSignIn?.setEnabled(false)

        // Simulate API call (here just dummy check)
        btnSignIn?.postDelayed({
            progressBar?.visibility = View.GONE
            btnSignIn?.setEnabled(true)
            if (email == KEY_ADMIN_EMAIL && password == KEY_ADMIN_PASSWORD) {
                // Save login state
                sharedPreferences?.edit {
                    this.putBoolean(KEY_LOGGED_IN, true)
                    this.putString(KEY_EMAIL, email)
                }

                Toast.makeText(this@AdminActivity, "Login Successful", Toast.LENGTH_SHORT).show()
               startActivity(ScanQrCodeActivity.getScanQrCodeActivity(this).apply {
                    putExtra(IS_ADMIN_DATA, true)
                })
                finish()
            } else {
                Toast.makeText(this@AdminActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }, 1500) // simulate network delay
    }

    companion object {
        private const val PREF_NAME = "LoginPref"
        private const val KEY_EMAIL = "email"
        private const val KEY_LOGGED_IN = "isLoggedIn"
    }
}
