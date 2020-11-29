package com.inplace.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.view.iterator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inplace.R
import com.inplace.auth.ui.login.*

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
}