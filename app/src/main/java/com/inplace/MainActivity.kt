package com.inplace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.inplace.chats.ItemFragment
import com.inplace.chats.NumberFragment

class MainActivity : AppCompatActivity(), SwitcherInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val transaction = supportFragmentManager.beginTransaction()
        if (savedInstanceState == null) {
            transaction.add(R.id.items, ItemFragment.newInstance(100))
            transaction.commitAllowingStateLoss()
        }
    }

    override fun switch(number: Int, color: Int) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.items, NumberFragment.newInstance(number, color))
            addToBackStack(null)
            commitAllowingStateLoss()
        }
    }

}