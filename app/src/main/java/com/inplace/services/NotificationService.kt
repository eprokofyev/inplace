package com.inplace.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.paging.ExperimentalPagingApi
import com.inplace.MainActivity
import com.inplace.R
import com.inplace.api.vk.ApiVk
import com.inplace.chats.ChatsByIdRepo
import com.inplace.models.SuperChat


@ExperimentalPagingApi
class NotificationService : Service() {

    companion object {
        fun startService(context: Context) {
            val startIntent = Intent(context, NotificationService::class.java)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, NotificationService::class.java)
            context.stopService(stopIntent)
        }
        const val EXTRAS_NAME = "Chats"
        const val BROADCAST_ACTION = "com.inplace.services"
        const val CHAT_FROM_NOTIFICATION = "chatToIntent"
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        val context = this as Context
    }

    private val NOTIFICATION_ID_FOREGROUND = 1
    private val NOTIFICATION_ID_MESSAGE = 2
    private val LIGHT_COLOR_ARGB = R.color.purple_500
    private val CHANNEL_MESSAGES = "messages"
    private val CHANNEL_FOREGROUND = "foreground"
    private val CHANNEL_NAME = "Новое сообщение"
    private val CHANNEL_FOREGROUND_NAME = "Состояние"
    private var mMessageCount = 0
    private lateinit var mManager: NotificationManager
   // private val intentToActivity = Intent(this, MainActivity::class.java)


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        val builderForForeground = NotificationCompat.Builder(this, CHANNEL_FOREGROUND)
            .setSmallIcon(R.drawable.ic_tick)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.for_foreground))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setColor(resources.getColor(R.color.purple_500))
        startForeground(NOTIFICATION_ID_FOREGROUND, builderForForeground.build())

        //Уведомления
        // Подписка на новые сообщения

        ExecutorServices.getInstanceAPI().execute() {

            //    CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val newMesgsResult = ApiVk.getNewMessages()
                Log.d("ApiVK", "end of get new message request")
                if (newMesgsResult.error != null) {
                    Log.d("ApiVK", newMesgsResult.errTextMsg)
                    continue
                }

                val newMessagesArray = newMesgsResult.result
                if (newMessagesArray.isNotEmpty()) {
                    val repo = ChatsByIdRepo()
                    val vkChats = repo.refresh(newMessagesArray)
                    if (vkChats.isEmpty()) {
                        Log.d("ApiVK", "vkchats is empty")
                        continue
                    }
                    Log.d("after", "after")
                    //val vkChatsArray = vkChats.toTypedArray()
                    val myIntent = Intent(BROADCAST_ACTION)
                    myIntent.putExtra(EXTRAS_NAME, newMessagesArray)
                    sendBroadcast(myIntent);
                    for (el in newMessagesArray) {
                        for (chat in vkChats){
                            if (chat.lastMessage.messageID == el.messageID) {
                                showMessageNotification(el.text, chat)
                            }
                        }

                    }
                }
                Thread.sleep(3000)
            }
        }
        return START_REDELIVER_INTENT
    }

    private fun showMessageNotification(messageToShow: String, chatToIntent: SuperChat) {
        val intentToActivity = Intent(this, MainActivity::class.java)
        //intentToActivity.putExtra(CHAT_FROM_NOTIFICATION, chatToIntent)
        val pendingIntent = PendingIntent.getActivity(this, 0, intentToActivity, 0)
        mMessageCount++
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
        builder.setStyle(style)
        mManager.notify(NOTIFICATION_ID_MESSAGE, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_FOREGROUND,
                CHANNEL_FOREGROUND_NAME,
                NotificationManager.IMPORTANCE_NONE
            )
            mManager.createNotificationChannel(serviceChannel)
            val notificationChannel = NotificationChannel(
                CHANNEL_MESSAGES, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
                lightColor = LIGHT_COLOR_ARGB
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            mManager.createNotificationChannel(notificationChannel)
        }
    }
}