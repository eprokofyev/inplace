package com.inplace

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.inplace.api.vk.ApiVk
import com.inplace.chat.ChatFragment
import com.inplace.chats.SwitcherInterface
import com.inplace.models.*
import com.inplace.services.ExecutorServices
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback

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
            Message(1234, 12425, "sdghg", 124, 32534, true, Source.TELEGRAM, true, arrayListOf()),
            HashMap(),
            1234
        )
        val map = HashMap<Long, VKChat>()
        map[1234] = vkChat

        chat = SuperChat(
            "Zarrukh Zoirzoda",
            "https://sun9-60.userapi.com/impf/mqqDx5yzyNAQRDgtOENjuoGMmr5aZWdmEYjY7Q/e6loTWETEOc.jpg?size=871x1080&quality=96&sign=22e5c81571b3cc8d6260d8ffa7fd0b34&type=album",
            Message(1234, 12425, "sdghg", 124, 32534, true, Source.TELEGRAM, true, arrayListOf()),
            true,
            map,
            hashMapOf(),
            12345,
            12324
        )
            switch(chat)


        //Уведомления
        val NOTIFICATION_ID_MESSAGE = 1
        val LIGHT_COLOR_ARGB = R.color.purple_500
        val CHANNEL_MESSAGES = "messages"
        var mMessageCount = 0
        val mManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        lateinit var notificationChannel : NotificationChannel
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        fun showMessageNotification(messageToShow: String) {
            mMessageCount++
            // val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.example_large_icon)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                notificationChannel = NotificationChannel(
                    CHANNEL_MESSAGES, "test", NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.enableVibration(false)
                mManager.createNotificationChannel(notificationChannel)
            }
            val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.foto)
            val builder = NotificationCompat.Builder(this, CHANNEL_MESSAGES)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.ic_send)
                .setContentTitle(getString(R.string.message_name))
                .setContentText(messageToShow)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLights(resources.getColor(LIGHT_COLOR_ARGB), 1000, 1000)
                .setColor(resources.getColor(R.color.purple_500))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
            val style = NotificationCompat.BigTextStyle()
            style.bigText(messageToShow)
//    style.setSummaryText(getString(R.string.message_summary, mMessageCount))
            builder.setStyle(style)
//    addDefaultIntent(builder)
//    addMessageIntent(builder, messageToShow)
//    mManager = NotificationManagerCompat.from(this@MainActivity)
            mManager.notify(NOTIFICATION_ID_MESSAGE, builder.build())
        }



        // Подписка на новые сообщения
        ExecutorServices.getInstanceAPI().execute()
        {
            while (true) {

                val newMesgsResult = ApiVk.getNewMessages()
                Log.d("ApiVK", "end of get new message request")

                val newMessagesArray = newMesgsResult.result

                for (el in newMessagesArray) {
                    showMessageNotification(el.text)
                }
                Thread.sleep(3000)
            }
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