package com.inplace.auth

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.inplace.api.vk.*
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import kotlin.concurrent.thread
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.view.iterator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import com.inplace.MainActivity
import com.inplace.R
import com.inplace.auth.ui.login.*

@ExperimentalPagingApi
class AuthActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_auth)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.auth_fragment, TgLoginFragment())
                .commit()
        }
        val messengers: RadioGroup = findViewById(R.id.choose_messenger)
        messengers.setOnCheckedChangeListener { group, checkedId ->
            for (i in group) {
                i.alpha = 0.5f
            }
            findViewById<RadioButton>(checkedId).alpha = 1f
            if (checkedId == R.id.tg_auth) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.auth_fragment, TgLoginFragment())
                    .commit()
            }
            if (checkedId == R.id.vk_auth) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.auth_fragment, VkLoginFragment())
                    .commit()

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("odred", "result")
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                Log.d("tokenn", token.toString())
                updateUiWithUser()
            }

            override fun onLoginFailed(errorCode: Int) {
                Log.d("tokenn", "error")
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun updateUiWithUser() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}