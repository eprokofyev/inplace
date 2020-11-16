package com.inplace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.inplace.chat.ChatFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null)
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, ChatFragment()).commit()
    }

    override fun onBackPressed() {
        when(supportFragmentManager.backStackEntryCount){
            0 -> {
                super.onBackPressed()
                finish()
            }
            else -> supportFragmentManager.popBackStack()
        }
    }
}