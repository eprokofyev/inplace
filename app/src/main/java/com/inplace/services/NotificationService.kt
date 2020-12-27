package com.inplace.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.paging.ExperimentalPagingApi
import com.inplace.MainActivity
import com.inplace.R
import com.inplace.api.vk.ApiVk
import com.inplace.chats.ChatsByIdRepo
import com.inplace.chats.ChatsRepo
import com.inplace.db.AppDatabase
import com.inplace.models.SuperChat
import com.inplace.models.VKChat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    lateinit var mManager: NotificationManager
    private val intentToActivity = Intent(this, MainActivity::class.java)


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
                    val chatIds: ArrayList<Int> = ArrayList()

                    for (el in newMessagesArray) {
                        showMessageNotification(el.text)
                        chatIds.add(el.chatID.toInt())
                    }
                    for (i in chatIds) {
                        Log.d("ApiVK", i.toString())
                    }
                    val repo = ChatsByIdRepo()
                    val vkChats = repo.refresh(chatIds)
                    if (vkChats.isNotEmpty()) {
                        Log.d("ApiVK", "end of get chats by id")
                        continue
                    }
                    Log.d("Chats", vkChats.toString())
                    //val vkChatsArray = vkChats.toTypedArray()
                    val myIntent = Intent(this@NotificationService, MainActivity::class.java)
                    myIntent.putExtra(EXTRAS_NAME, newMessagesArray)
                    sendBroadcast(myIntent);
                }
                Thread.sleep(3000)
            }
        }
        return START_REDELIVER_INTENT
    }

    private fun showMessageNotification(messageToShow: String) {
        val pendingIntent = PendingIntent.getActivity(
            this as Context,
            0,
            intentToActivity,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

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
            mManager = getSystemService(NotificationManager::class.java)
            val serviceChannel = NotificationChannel(CHANNEL_FOREGROUND, CHANNEL_FOREGROUND_NAME, NotificationManager.IMPORTANCE_NONE)
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