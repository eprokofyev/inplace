package com.inplace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

import androidx.paging.ExperimentalPagingApi


import com.inplace.chats.ChatsFragment
import com.inplace.api.vk.ApiVk
import com.inplace.chat.ChatFragment
import com.inplace.chats.SwitcherInterface
import com.inplace.models.*
import com.inplace.services.NotificationService
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback

@ExperimentalPagingApi
class MainActivity : AppCompatActivity(), SwitcherInterface {
    lateinit var chat: SuperChat

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
               switch(chat)
            }

            override fun onLoginFailed(errorCode: Int) {

            }
        }
        if ((data == null || !VK.onActivityResult(
                requestCode,
                resultCode,
                data,
                callback
            ))) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //NotificationService.startService(this)
        setContentView(R.layout.activity_main)
        Log.d("start", "start")

        val transaction = supportFragmentManager.beginTransaction()
        if (savedInstanceState == null) {
            transaction.add(R.id.fragment_container, ChatsFragment.newInstance())
            transaction.commitAllowingStateLoss()
        }
    }
//val intentToNotification = Intent(this, NotificationService::class.java)
    override fun onStop() {
        super.onStop()
       // NotificationService.startService(this)
    }
    override fun onRestart() {
        super.onRestart()
        //NotificationService.stopService(this)
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