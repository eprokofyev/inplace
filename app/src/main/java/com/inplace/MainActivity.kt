package com.inplace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

import com.inplace.chat.ChatFragment
import com.inplace.chats.SwitcherInterface
import com.inplace.models.*
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope

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
        setContentView(R.layout.activity_main)
        Log.d("start", "start")
        val vkChat = VKChat(
            443110568,
            Source.VK,
            "Zarrukh Zoirzoda",
            null,
            "https://sun9-60.userapi.com/impf/mqqDx5yzyNAQRDgtOENjuoGMmr5aZWdmEYjY7Q/e6loTWETEOc.jpg?size=871x1080&quality=96&sign=22e5c81571b3cc8d6260d8ffa7fd0b34&type=album",
            mutableListOf(),
            true,
            ChatType.PRIVATE,
            Message(1234,12425,"sdghg",124,32534,true,Source.TELEGRAM,true, arrayListOf()),
            HashMap(),
            1234
        )
        val map = HashMap<Long,VKChat>()
        map[1234] = vkChat

        chat = SuperChat(
            "Zarrukh Zoirzoda",
            "https://sun9-60.userapi.com/impf/mqqDx5yzyNAQRDgtOENjuoGMmr5aZWdmEYjY7Q/e6loTWETEOc.jpg?size=871x1080&quality=96&sign=22e5c81571b3cc8d6260d8ffa7fd0b34&type=album",
            Message(1234,12425,"sdghg",124,32534,true,Source.TELEGRAM,true, arrayListOf()),
            true,
            map,
            hashMapOf(),
            12345,
            12324
        )
        val loggedIn = VK.isLoggedIn()

        if(!loggedIn){
            VK.login(
                this@MainActivity, arrayListOf(
                    VKScope.FRIENDS,
                    VKScope.EMAIL,
                    VKScope.WALL,
                    VKScope.PHOTOS,
                    VKScope.MESSAGES,
                    VKScope.DOCS,
                    VKScope.GROUPS,
                    VKScope.PAGES,
                    VKScope.MESSAGES,
                    VKScope.OFFLINE
                )
            )
        }else{
            switch(chat)
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