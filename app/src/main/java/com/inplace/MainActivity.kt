package com.inplace

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.inplace.api.vk.ApiVK
import com.inplace.chat.ChatFragment
import com.inplace.chats.ChatsFragment
import com.inplace.chats.SwitcherInterface

class MainActivity : AppCompatActivity(), SwitcherInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val transaction = supportFragmentManager.beginTransaction()
        if (savedInstanceState == null) {
            transaction.add(R.id.fragment_container, ChatsFragment.newInstance(100))
            transaction.commitAllowingStateLoss()
        }
    }

    override fun switch(number: Int, color: Int) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, ChatFragment())
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