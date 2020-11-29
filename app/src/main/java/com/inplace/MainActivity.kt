package com.inplace

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.inplace.api.vk.ApiVK
import com.inplace.chat.ChatFragment
import com.inplace.chats.ItemFragment
import com.inplace.chats.SwitcherInterface
import com.inplace.models.*

class MainActivity : AppCompatActivity(), SwitcherInterface {
    val LOGIN_STATE = "loginState"
    var loggedIn = false
    val LOG_TAG = "vkApi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loggedIn = savedInstanceState?.getBoolean(LOGIN_STATE) ?: false

        if (!loggedIn) {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val name = ""
        val pass = ""

            val loginResult = ApiVK.login(name, pass)
            if (loginResult.error == null) {
                Log.d(LOG_TAG, "Successfully logged in. Your id: ${loginResult.result}")
                loggedIn = true
            } else {
                Log.d(LOG_TAG, "Error while logging in: ${loginResult.errTextMsg}")
            }
        } else {
            Log.d(LOG_TAG, "Already logged in")
        }


        val transaction = supportFragmentManager.beginTransaction()
        if (savedInstanceState == null) {
            transaction.add(R.id.fragment_container, ItemFragment.newInstance(100))
            transaction.commitAllowingStateLoss()
        }
    }

    override fun switch(number: Int, color: Int) {
        val me = User(
                "",
                "",
                null, null, 23435654)

        val sobesedniks = mutableListOf<Sobesednik>(Sobesednik(
                "",
                "",
                SobesednikVk("name","","","online","",1234), null, "36452345"
        ))

        var messagesList = mutableListOf<com.inplace.models.Message>()


        val chat: Chat = Chat(me, sobesedniks, messagesList, true, "3425346", "1234", Source.VK, "1234")
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(LOGIN_STATE, loggedIn)
    }

    override fun onDestroy() {
        super.onDestroy()


        if (loggedIn) {
            val returnValue = ApiVK.logout()
            if (returnValue.error == null) {
                Log.d(LOG_TAG, "Successfully logged out")
            } else {
                Log.d(LOG_TAG, "Error while logging out: ${returnValue.errTextMsg}")
            }
        }


    }

}