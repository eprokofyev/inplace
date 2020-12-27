package com.inplace

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import androidx.paging.ExperimentalPagingApi
import com.inplace.api.ApiImageLoader
import com.inplace.api.CommandResult


import com.inplace.chats.ChatsFragment
import com.inplace.api.vk.ApiVk
import com.inplace.api.vk.VkChat
import com.inplace.api.vk.VkChatWithUsers
import com.inplace.chat.ChatFragment
import com.inplace.chats.SwitcherInterface
import com.inplace.models.*
import com.inplace.services.NotificationService
import com.vk.api.sdk.*
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.utils.log.DefaultApiLogger
import com.vk.api.sdk.utils.log.Logger
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    var myAtaches: ArrayList<Uri> = ArrayList<Uri>()
    var idLastChatWithId = -1; // в эту переменную запишем последний чат пользователя (его id)
    val RESULT_LOAD_IMAGE = 55 // some code for pick images action


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // part for vk login
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                val textID = resources.getIdentifier("textId", "id", packageName)
                val myText: TextView = findViewById(textID)
                myText.setText("good login")
            }

            override fun onLoginFailed(errorCode: Int) {
                val textID = resources.getIdentifier("textId", "id", packageName)
                val myText: TextView = findViewById(textID)
                myText.setText("bad login")
            }
        }
        if (requestCode !== RESULT_LOAD_IMAGE && (data == null || !VK.onActivityResult(
                requestCode,
                resultCode,
                data,
                callback
            ))) {
            super.onActivityResult(requestCode, resultCode, data)
        }


        // part for load image
        if (requestCode === RESULT_LOAD_IMAGE && resultCode === RESULT_OK) {

            myAtaches.clear() // clear previous images
            Log.e("URI", "result ok get images")

            try {
                if(data!=null)
                {
                    Log.e("URI", "data != null")
                    val clipData: ClipData? = data.getClipData()

                    //if many images selected
                    if (clipData != null) {
                        Log.d("onActivityResult", "отправка себе нескольких фоток")
                        for (i in 0 until clipData.itemCount) {
                            val imageUri = clipData.getItemAt(i).uri
                            Log.e("URI", imageUri.toString())
                            myAtaches.add(imageUri)
                        }
                        val result = ApiVk.sendMessage(VK.getUserId(), "", myAtaches)
                        if (result.error != null) {
                            Log.e("err", "err load images:" + result.error)
                        } else {
                            Log.e("new msg", "id:" + result.result.toString())
                        }

                        return
                    }


                    // if only ONE image
                    val filePath = data.getData();
                    if (filePath != null) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),
                            data.getData()
                        );
                        val myAvatarId = resources.getIdentifier("myAvatar", "id", packageName)
                        val myAvatarView: ImageView = findViewById(myAvatarId)
                        myAvatarView.setImageBitmap(bitmap)

                        // set attach in
                        myAtaches.add(filePath)
                        Log.d("onActivityResult", "отправка себе одной фотки")
                        val result = ApiVk.sendMessage(VK.getUserId(), "", myAtaches)
                        if (result.error != null) {
                            Log.e("err", "err load one image:" + result.error)
                        } else {
                            Log.e("new msg", "id:" + result.result.toString())
                        }
                    }
                }
                else
                {
                    // user simply backpressed from gallery
                }
            } catch (e: Exception) {
                e.printStackTrace();
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT > 9) {
            val policy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }


        // можно вызвать в любом месте,
        // для eng - не указывать параметр lang
        VK.setConfig(VKApiConfig(
            context = applicationContext,
            appId = R.integer.com_vk_sdk_AppId,
            validationHandler = VKDefaultValidationHandler(applicationContext),
            lang = "ru",
        ))



        val textID = resources.getIdentifier("textId", "id", packageName)

//        VK.initialize(getApplicationContext());
//
//        val returnKeys = VKUtils.getCertificateFingerprint(this, this.getPackageName())
//
//        if (returnKeys != null) {
//            if (returnKeys.isNotEmpty()) {
//                Log.e("key:", returnKeys[0].toString())
//            }
//        }

        // авторизация
        val buttonIDauth = resources.getIdentifier("authBtn", "id", packageName)
        val authBtn: Button = findViewById(buttonIDauth)
        val authBtnListen: View.OnClickListener = object : View.OnClickListener {

            override fun onClick(v: View?) {





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
            }
        }
        authBtn.setOnClickListener(authBtnListen)




        // logout
        val buttonIDLogout = resources.getIdentifier("logoutBtn", "id", packageName)
        val myBtnLogout: Button = findViewById(buttonIDLogout)
        val logOutButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val returnValue = ApiVk.logout()
                val myText: TextView = findViewById(textID)
                if (returnValue.error == null) {
                    myText.setText("Успешный выход")
                } else {
                    myText.setText("Ошибка при выходе")
                }
            }
        }
        myBtnLogout.setOnClickListener(logOutButtonClickListener)



        // получения пользователя
        val buttonIDGetMe = resources.getIdentifier("getMe", "id", packageName)
        val myAvatarId = resources.getIdentifier("myAvatar", "id", packageName)
        val myAvatarView: ImageView = findViewById(myAvatarId)
        val myBtnGetMe: Button = findViewById(buttonIDGetMe)
        val GetMeButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val userRestul = ApiVk.getMe()
                Log.d("ApiVK", "end of logout request")
                val myText: TextView = findViewById(textID)
                if (userRestul.error != null) {
                    myText.setText("Ошибка в запросе о себе: " + userRestul.errTextMsg)
                    return
                }

                val user = userRestul.result

                val showText = "Имя:" + user.firstName + "\nФамилия:" + user.lastName +
                        "\nid:" + user.id + "\t isClosed:" + user.isClosed.toString() +
                        "\nСтатус:" + user.status + "\nОбо мне:" + user.about +
                        "\nOnline:" + user.online.toString() +
                        "\n Аватарка 200px:" + user.photo200Square;

                myText.setText(showText)
                myAvatarView.setImageBitmap(
                    ApiImageLoader.getInstance(this@MainActivity).getImageByUrl(
                        user.photo200Square
                    )
                );

            }
        }
        myBtnGetMe.setOnClickListener(GetMeButtonClickListener)




        // получение чатов
        val idChatArray = java.util.ArrayList<Int>()

        val buttonIDGetChats = resources.getIdentifier("chatBtn", "id", packageName)
        val myBtnGetChats: Button = findViewById(buttonIDGetChats)
        val GetChatsButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            @SuppressLint("UseValueOf")
            override fun onClick(v: View?) {
                idChatArray.clear()
                val myChatsResult = ApiVk.getChats(0, 5)
                Log.d("ApiVK", "end of chats request")
                val myText: TextView = findViewById(textID)
                if (myChatsResult.error != null) {
                    myText.setText("Ошибка получечния чатов или у вас их нет =)")
                    return
                }

                val chatsWithUsers = myChatsResult.result

                val myChats = chatsWithUsers.chats
                val usersMap = chatsWithUsers.users


                var showText = ""
                if (myChats != null) {
                    for (el in myChats) {

                        var type = ""
                        if (el.chatType == VkChat.CHAT_TYPE_USER) {
                            type = "user"
                        }
                        if (el.chatType == VkChat.CHAT_TYPE_GROUP_CHAT) {
                            type = "group chat title:" + el.groupChatTitle

                        }

                        showText += "type:" + type + "\t date:" + el.date + "\t last msg from id user:" + el.lastMsgFromId +
                                "\n chat with id:" + el.chatWithId + "\t text:" + el.text + "\nlast msg id:" + el.lasMsgId + "\t unread count:"  + el.unreadСount

                        val vkUser = usersMap?.get(el.chatWithId)

                        if (vkUser != null) {
                            showText += " user info:: name:" + vkUser.firstName + " " + vkUser.lastName +
                                    " "
                        }


                        showText += "\n-----------------------\n"

                        idChatArray.add(el.chatWithId)
                    }
                }




                if (myChats != null) {
                    idLastChatWithId = myChats.get(0).chatWithId
                }
                myText.setText(showText)
                Log.d("ApiVK", "idChatArray size():" + idChatArray.size)

            }
        }
        myBtnGetChats.setOnClickListener(GetChatsButtonClickListener)



        // получение истории сообщений
        val buttonIDGetMsg = resources.getIdentifier("openLastChat", "id", packageName)
        val myBtnGetMsg: Button = findViewById(buttonIDGetMsg)
        val GetMsgsButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val resultGetMessages = ApiVk.getMessages(idLastChatWithId, 0, 10)
                Log.d("ApiVK", "end of get last chat messages request")
                val myText: TextView = findViewById(textID)
                if (resultGetMessages.error != null) {
                    myText.setText("Ошибка получечния сообщений:" + resultGetMessages.error)
                    return
                }
                var showText = "";

                val messagesArray = resultGetMessages.result

                for (el in messagesArray) {

                    showText += "message fromId:" + el.userID + "\tdata:" + el.date +
                            "\t isMymsg:" + el.myMsg + "\ntext:" + el.text + "\nmsg id:" + el.messageID

                    if (el.photos.size > 0) {
                        showText += "\n phto URL:" + el.photos[0]
                    }
                    showText += "\n-----------------------\n"

                }
                myText.setText(showText)

//                if (messagesArray.size > 0 && messagesArray[0].photos[0] != null) {
//                    myAvatarView.setImageBitmap(
//                        ApiImageLoader.getInstance(this@MainActivity).getImageByUrl(
//                            messagesArray[0].photos[0]
//                        )
//                    );
//                }


            }
        }
        myBtnGetMsg.setOnClickListener(GetMsgsButtonClickListener)



        // отправка сообщения
        val buttonIDSendMsg = resources.getIdentifier("sendMsg", "id", packageName)
        val myBtnSendMsg: Button = findViewById(buttonIDSendMsg)

        val iDInput = resources.getIdentifier("msgToSend", "id", packageName)
        val inputForMsg: EditText = findViewById(iDInput)

        val SendMsgsButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {

                val msg = inputForMsg.text.toString()

                val sendMsgResult = ApiVk.sendMessage(idLastChatWithId, msg, ArrayList<Uri>())

                Log.d("ApiVK", "end of send message request")

                val myText: TextView = findViewById(textID)
                if (sendMsgResult.error != null) {
                    myText.setText("Ошибка отправки сообщения")
                    return
                }

                val messageId = sendMsgResult.result

                myText.setText(
                    "Сообщение отправлено, проверить - получите чаты, а затем сообщения в последнем чате" +
                            "\t id отправленного сообщения:" + messageId
                )
            }
        }
        myBtnSendMsg.setOnClickListener(SendMsgsButtonClickListener)






        // Получение пользователей
        val buttonIDGetUsers = resources.getIdentifier("getUsersBtn", "id", packageName)
        val myBtnGetUsers: Button = findViewById(buttonIDGetUsers)
        val GetUsersButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val myText: TextView = findViewById(textID)
                if (idChatArray.size == 0) {
                    myText.setText("Ошибка получечния чатов или у вас их нет =)")
                    return
                }
                Log.d("ApiVK", "send idChatArray size():" + idChatArray.size)
                val getUsersResult = ApiVk.getUsers(idChatArray)
                Log.d("ApiVK", "end of get last chat messages request")

                if (getUsersResult.error != null) {
                    myText.setText("Ошибка получечния инфы о собеседниках")
                    return
                }
                var showText = "Порядок как в списке чатов, фотку ставим первого собеседника\n";

                val usersArray = getUsersResult.result

                for (el in usersArray) {

                    showText += "Имя:" + el.firstName + "\t фамилия:" + el.lastName + "\tid:" + el.id +
                            "\nstatus:" + el.status + "\t о пользователе" + el.about + "\tisClosed:"+ el.isClosed+
                            "\n online:" + el.online

                    showText += "\n-----------------------\n"
                }
                myText.setText(showText)
                myAvatarView.setImageBitmap(
                    ApiImageLoader.getInstance(this@MainActivity).getImageByUrl(
                        usersArray[0].photo200Square
                    )
                );
            }
        }
        myBtnGetUsers.setOnClickListener(GetUsersButtonClickListener)







        // Подписка на новые сообщения
        val buttonIDGetNewMsg = resources.getIdentifier("waitMsg", "id", packageName)
        val GetNewMsgSendMsg: Button = findViewById(buttonIDGetNewMsg)

        var isStartedThread = false

        val GetNewMsgButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {

                if (isStartedThread)
                    return

                isStartedThread = true
                var numberOfRequest = 0

                val myText: TextView = findViewById(textID)

                thread {
                    while (true) {

                        val newMesgsResult = ApiVk.getNewMessages()
                        numberOfRequest++
                        Log.d("ApiVK", "end of get new message request")

                        if (newMesgsResult.error != null) {
                            myText.setText("errr get new msgs:" + newMesgsResult.error)
                            return@thread
                        }

                        val newMessagesArray = newMesgsResult.result

                        var messagesText = ""

                        for (el in newMessagesArray) {
                            messagesText += "message fromId:" + el.userID + "\tdata:" + el.date +
                                    "\t isMymsg:" + el.myMsg + "\ntext:" + el.text + "\n msgId:" + el.messageID
                            messagesText += "\n-----------------------\n"
                        }


                        val showText = "Номер запроса:" + numberOfRequest + "\tВаши новые сообещния:\n" +
                                messagesText

                        myText.setText(showText)
                        Thread.sleep(3000)
                    }
                }

            }
        }
        GetNewMsgSendMsg.setOnClickListener(GetNewMsgButtonClickListener)






        val buttonShowImg = resources.getIdentifier("getChatsbyId", "id", packageName)
        val showImgBtn: Button = findViewById(buttonShowImg)


        val showImageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {

                thread {

                var ids: ArrayList<Int> = ArrayList<Int>();
                ids.add(185205306)
                val result: CommandResult<VkChatWithUsers> = ApiVk.getConversationsById(ids)

                val myText: TextView = findViewById(textID)
                var showTest = ""

                if (result.error != null) {
                    myText.setText("err get chats by id:" + result.error)
                } else {

                    val chats = result.result.chats

                    if (chats != null) {
                        for (el in chats) {
                            showTest += "chatTitle:" + el.groupChatTitle
                            showTest += "\nchat id:" + el.chatWithId
                            showTest += "\nunreadСount:" + el.unreadСount
                        }
                    } else {
                        showTest += "chats == null"
                    }

                    val users = result.result.users

                    if (users != null) {
//                        showTest += "\nuser:" + result.result.users!!.get(136034172)!!.firstName
//                        showTest += "\t" + result.result.users!!.get(136034172)!!.lastName
//                        showTest += "\nid:" + result.result.users!!.get(136034172)!!.id
                    }

                    myText.setText(showTest)
                }
            }

            }
        }
        showImgBtn.setOnClickListener(showImageClickListener)



        val buttonLoadImg = resources.getIdentifier("loadPhoto", "id", packageName)
        val loadImgBtn: Button = findViewById(buttonLoadImg)
        val loadImageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {


                Log.d("onClick", "pick image click()")
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.type = "image/*"
                startActivityForResult(intent, RESULT_LOAD_IMAGE)


            }
        }
        loadImgBtn.setOnClickListener(loadImageClickListener)



        //markAsRead
        val buttonMarkRead = resources.getIdentifier("markAsRead", "id", packageName)
        val MarkReadBtn: Button = findViewById(buttonMarkRead)
        val MarkReadClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {

                val myText: TextView = findViewById(textID)

                // hardcore id

                val cr = ApiVk.markAsRead(185205306);
                if (cr.error == null) {
                    myText.setText("markAsRead errr:" + cr.error)
                } else {
                    myText.setText("markAsRead is OK:")
                }

            }
        }
        MarkReadBtn.setOnClickListener(MarkReadClickListener)








    }




}

