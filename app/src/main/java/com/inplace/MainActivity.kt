package com.inplace


import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.paging.ExperimentalPagingApi
import com.inplace.chat.ChatFragment
import com.inplace.chats.ChatsFragment
import com.inplace.chats.NewMessageReceiver
import com.inplace.chats.SwitcherInterface
import com.inplace.models.SuperChat
import com.inplace.services.NotificationService
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiConfig
import com.vk.api.sdk.VKDefaultValidationHandler
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback


@ExperimentalPagingApi
class MainActivity : AppCompatActivity(), SwitcherInterface {
    lateinit var chat: SuperChat

    var status = false

    lateinit var newMessageReceiver: BroadcastReceiver

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
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
            ))
        ) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        status = savedInstanceState?.getBoolean("status") ?: false

        if (!status) {
            VK.setConfig(
                VKApiConfig(
                    context = applicationContext,
                    appId = R.integer.com_vk_sdk_AppId,
                    validationHandler = VKDefaultValidationHandler(applicationContext),
                    lang = "ru",
                )
            )
            status = true
        }

        //NotificationService.startService(this)
        //val notificationService = Intent(this, NotificationService::class.java)
        //this.startService(notificationService)
        val startIntent = Intent(this, NotificationService::class.java)
        if (!isMyServiceRunning(NotificationService::class.java)) {
            ContextCompat.startForegroundService(this, startIntent)
        }
        setContentView(R.layout.activity_main)
        Log.d("start", "start")

        val transaction = supportFragmentManager.beginTransaction()
        if (savedInstanceState == null) {
            transaction.add(R.id.fragment_container, ChatsFragment.newInstance())
            transaction.commitAllowingStateLoss()
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        if (intent.hasExtra(NotificationService.CHAT_FROM_NOTIFICATION)) {
            intent.getParcelableExtra<SuperChat>(NotificationService.CHAT_FROM_NOTIFICATION)?.let {
                switch(
                    it
                )
            }
        }
        NotificationService.highImportance = false
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

        outState.putBoolean("status", status)
    }

    override fun onStop() {
        NotificationService.highImportance = true
        super.onStop()
    }

    override fun switch(chat: SuperChat) {
        NotificationService.notificationToClear = chat.lastMessage.userID.toInt()
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