package com.inplace.services

import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.os.Message
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.paging.ExperimentalPagingApi
import com.inplace.MainActivity
import com.inplace.R
import com.inplace.api.ApiImageLoader
import com.inplace.api.vk.ApiVk
import com.inplace.chats.ChatsByIdRepo
import com.inplace.models.SuperChat
import com.vk.api.sdk.VK
import java.util.*


@ExperimentalPagingApi
class NotificationService : Service() {

    companion object {
        var notificationToClear = -1
        var highImportance = false
        const val EXTRAS_NAME = "Chats"
        const val CHAT_FROM_NOTIFICATION = "chatToIntent"
        const val BROADCAST_ACTION = "com.inplace.services.NotificationService"

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private val NOTIFICATION_ID_FOREGROUND = 1
    private val NOTIFICATION_ID_MESSAGE = 2
    private val LIGHT_COLOR_ARGB = R.color.purple_500
    private val CHANNEL_MESSAGES_LOW = "messages_low"
    private val CHANNEL_MESSAGES_HIGH = "messages_high"
    private val CHANNEL_FOREGROUND = "foreground"
    private val CHANNEL_NAME_LOW = "Сообщение (приложение открыто)"
    private val CHANNEL_NAME_HIGH = "Сообщение (приложение закрыто)"

    private val CHANNEL_FOREGROUND_NAME = "Состояние"
    private var mMessageCount = 0
    private lateinit var mManager: NotificationManager

    private val loader = ApiImageLoader.getInstance(this)


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

        ExecutorServices.getInstanceAPI().execute {
            while (true) {
                val newMesgsResult = ApiVk.getNewMessages()
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

                    if (newMessagesArray.size > 0) {
                        val myIntent = Intent(BROADCAST_ACTION)
                        myIntent.putExtra(EXTRAS_NAME, newMessagesArray)
                        sendBroadcast(myIntent);
                    }
                    for (el in newMessagesArray) {
                        for (chat in vkChats) {
                            if ((chat.lastMessage.messageID == el.messageID) && (el.userID.toInt() != VK.getUserId())) {
                                showMessageNotification(el, chat)
                            }
                        }
                    }
                }
                Thread.sleep(1000)
                if (notificationToClear != -1) {
                    mManager.cancel(notificationToClear)
                    notificationToClear = -1
                }
            }
        }
        return START_REDELIVER_INTENT
    }

    private fun showMessageNotification(message: com.inplace.models.Message, chatToIntent: SuperChat) {
        val intentToActivity = Intent(this, MainActivity::class.java)
        intentToActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intentToActivity.putExtra(CHAT_FROM_NOTIFICATION, chatToIntent)
        val pendingIntent = PendingIntent.getActivity(
            this,
            UUID.randomUUID().hashCode(),
            intentToActivity,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        mMessageCount++
        val largeIcon = if (chatToIntent.avatarURL.isEmpty()) {
            BitmapFactory.decodeResource(resources, R.drawable.foto)
        } else {
            loader.getImageByUrl(chatToIntent.avatarURL)
        }
        val builder = if(highImportance) {
            NotificationCompat.Builder(this, CHANNEL_MESSAGES_HIGH)
        } else {
            NotificationCompat.Builder(this, CHANNEL_MESSAGES_LOW)
        }
            .setLargeIcon(largeIcon)
            .setSmallIcon(R.drawable.ic_send)
            .setContentTitle(chatToIntent.title)
            .setContentText(message.text)
            .setLights(resources.getColor(LIGHT_COLOR_ARGB), 1000, 1000)
            .setColor(resources.getColor(R.color.purple_500))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        if (highImportance) {
            builder.priority = NotificationCompat.PRIORITY_HIGH
        } else {
            builder.priority = NotificationCompat.PRIORITY_LOW
        }
        val style = NotificationCompat.BigTextStyle()
        style.bigText(message.text)
        builder.setStyle(style)
        mManager.notify(message.userID.toInt(), builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_FOREGROUND,
                CHANNEL_FOREGROUND_NAME,
                NotificationManager.IMPORTANCE_NONE
            )
            mManager.createNotificationChannel(serviceChannel)
            val notificationHighPriorityChannel = NotificationChannel(
                CHANNEL_MESSAGES_HIGH, CHANNEL_NAME_HIGH,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
                lightColor = LIGHT_COLOR_ARGB
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            mManager.createNotificationChannel(notificationHighPriorityChannel)
            val notificationLowPriorityChannel = NotificationChannel(
                CHANNEL_MESSAGES_LOW, CHANNEL_NAME_LOW,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                lightColor = LIGHT_COLOR_ARGB
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            mManager.createNotificationChannel(notificationLowPriorityChannel)
        }
    }
    fun clear () {

    }
}