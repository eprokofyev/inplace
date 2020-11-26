package com.inplace

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.inplace.api.vk.ApiVK
import com.inplace.chat.ChatFragment
import com.inplace.chats.ItemFragment
import com.inplace.chats.SwitcherInterface
import com.inplace.models.Chat
import com.inplace.models.Sobesednik
import com.inplace.models.Source
import com.inplace.models.User

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

        val name = "89777146732"
        val pass = "qwerty123456ytrewq"

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
                "Махмуд Рахимов",
                "https://sun1-21.userapi.com/impf/c840239/v840239290/696d4/O7oxqSlag3o.jpg?size=608x1080&quality=96&sign=aa57a05355d03fa3b249639ac06eb936",
                null, null, 443110568)

        val sobesedniks = mutableListOf<Sobesednik>(Sobesednik(
                "Бахтиёр Мулладжанов",
                "https://sun1-92.userapi.com/impf/c849432/v849432158/1b5098/pyG0yYlzWHM.jpg?size=810x1080&quality=96&sign=914f8325bbe0eec2259128c754680374",
                null, null, "443318924"
        ))

        var messagesList = mutableListOf<com.inplace.models.Message>()


        val chat: Chat = Chat(me, sobesedniks, messagesList, true, "443318924", "1234", Source.VK, "1234")
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, ChatFragment(chat))
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