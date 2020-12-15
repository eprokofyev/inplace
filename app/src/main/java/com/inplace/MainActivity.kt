package com.inplace

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.inplace.api.vk.ApiVK

import com.inplace.chats.ChatsFragment
import com.inplace.chats.SwitcherInterface
import com.inplace.models.*

class MainActivity : AppCompatActivity(), SwitcherInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("start", "start")
        val transaction = supportFragmentManager.beginTransaction()
        if (savedInstanceState == null) {
            transaction.add(R.id.fragment_container, ChatsFragment.newInstance())
            transaction.commitAllowingStateLoss()
        }
    }

    override fun switch(chat: SuperChat) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, ChatFragment.newInstance(chat))
            addToBackStack(null)
            commitAllowingStateLoss()
        }


    }

    override fun onBackPressed() {
        when (supportFragmentManager.backStackEntryCount) {
            0 -> {
                super.onBackPressed()
                finish()
            }
            else -> supportFragmentManager.popBackStack()
        }
    }
}