package com.inplace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.paging.ExperimentalPagingApi
import com.inplace.auth.AuthActivity
import com.vk.api.sdk.VK

@ExperimentalPagingApi
class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = if (VK.isLoggedIn()) {
            Intent (this, MainActivity::class.java)
        } else {
            Intent (this, AuthActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}