package com.inplace

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.inplace.api.ApiImageLoader.getImageByUrl
import com.inplace.api.vk.ApiVK
import com.inplace.api.vk.VkChat
import com.inplace.api.vk.VkUser
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT > 9) {
            val policy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        val textID = resources.getIdentifier("textId", "id", packageName)



        // авторизация
        val buttonIDauth = resources.getIdentifier("authBtn", "id", packageName)
        val authBtn: Button = findViewById(buttonIDauth)
        val authBtnListen: View.OnClickListener = object : View.OnClickListener {

            override fun onClick(v: View?) {

                Log.d("ApiVK", "start of auth request")

                // todo hardcore name and pass
                val name: String = "89778932077"
                val pass: String = "b1\$bA7@a1fd*fcC8d[27d.d39975)bX}"

                val loginResult = ApiVK.login(name, pass)
                Log.d("ApiVK", "end of auth request")

                val myText: TextView = findViewById(textID)

                if (loginResult.error == null) {
                    myText.setText("Ваш id:"  + loginResult.result)
                } else {
                    myText.setText("Не удалось авторизоваться\n Ошибки:" + loginResult.errTextMsg)
                }

            }
        }
        authBtn.setOnClickListener(authBtnListen)



        // logout
        val buttonIDLogout = resources.getIdentifier("logoutBtn", "id", packageName)
        val myBtnLogout: Button = findViewById(buttonIDLogout)
        val logOutButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val returnValue = ApiVK.logout()
                Log.d("ApiVK", "end of logout request")
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
                val userRestul = ApiVK.getMe()
                Log.d("ApiVK", "end of logout request")
                val myText: TextView = findViewById(textID)
                if (userRestul.error != null) {
                    myText.setText("Ошибка в запросе о себе")
                    return
                }

                val user = userRestul.result as VkUser

                val showText = "Имя:" + user.firstName + "\nФамилия:" + user.lastName +
                               "\nid:" + user.id + "\t isClosed:" + user.isClosed.toString() +
                               "\nСтатус:" + user.status + "\nОбо мне:" + user.about +
                               "\nOnline:" + user.online.toString() +
                               "\n Аватарка 200px:" + user.photo200Square;

                myText.setText(showText)
                myAvatarView.setImageBitmap( getImageByUrl(user.photo200Square, this@MainActivity) );

            }
        }
        myBtnGetMe.setOnClickListener(GetMeButtonClickListener)




        // получение чатов

        var idLastChatWithId = -1; // в эту переменную запишем последний чат пользователя (его id)
        val idChatArray = java.util.ArrayList<Int>()

        val buttonIDGetChats = resources.getIdentifier("chatBtn", "id", packageName)
        val myBtnGetChats: Button = findViewById(buttonIDGetChats)
        val GetChatsButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            @SuppressLint("UseValueOf")
            override fun onClick(v: View?) {
                idChatArray.clear()
                val myChatsResult = ApiVK.getChats(0, 5)
                Log.d("ApiVK", "end of chats request")
                val myText: TextView = findViewById(textID)
                if (myChatsResult.error != null) {
                    myText.setText("Ошибка получечния чатов или у вас их нет =)")
                    return
                }

                val myChats = myChatsResult.result as java.util.ArrayList<VkChat>

                var showText = ""
                for (el in myChats) {

                    var type = ""
                    if (el.chatType == VkChat.CHAT_TYPE_USER) {
                        type = "user"
                    }
                    if (el.chatType == VkChat.CHAT_TYPE_GROUP_CHAT) {
                        type = "group chat"
                    }

                    showText += "type:" + type + "\t date:" + el.date + "\t last msg from id user:" + el.lastMsgFromId +
                      "\n chat with id:" + el.chatWithId + "\t text:" + el.text

                    showText += "\n-----------------------\n"

                    idChatArray.add(el.chatWithId)
                }
                idLastChatWithId = myChats.get(0).chatWithId
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
                val resultGetMessages = ApiVK.getMessages(idLastChatWithId, 0, 10)
                Log.d("ApiVK", "end of get last chat messages request")
                val myText: TextView = findViewById(textID)
                if (resultGetMessages.error != null) {
                    myText.setText("Ошибка получечния сообщений:" + resultGetMessages.error)
                    return
                }
                var showText = "";

                val messagesArray = resultGetMessages.result as java.util.ArrayList<com.inplace.api.Message>

                for (el in messagesArray) {

                    showText += "message fromId:" + el.fromId + "\tdata:" + el.date +
                    "\t isMymsg:" + el.myMsg + "\ntext:" + el.text

                    showText += "\n-----------------------\n"

                }
                myText.setText(showText)
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

                val sendMsgResult = ApiVK.sendMessage(idLastChatWithId, msg)

                Log.d("ApiVK", "end of send message request")

                val myText: TextView = findViewById(textID)
                if (sendMsgResult.error != null) {
                    myText.setText("Ошибка отправки сообщения")
                    return
                }
                myText.setText("Сообщение отправлено, проверить - получите чаты, а затем сообщения в последнем чате")
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
                val getUsersResult = ApiVK.getUsers(idChatArray)
                Log.d("ApiVK", "end of get last chat messages request")

                if (getUsersResult.error != null) {
                    myText.setText("Ошибка получечния инфы о собеседниках")
                    return
                }
                var showText = "Порядок как в списке чатов, фотку ставим первого собеседника\n";

                val usersArray = getUsersResult.result as java.util.ArrayList<VkUser>

                for (el in usersArray) {

                    showText += "Имя:" + el.firstName + "\t фамилия:" + el.lastName + "\tid:" + el.id +
                    "\nstatus:" + el.status + "\t о пользователе" + el.about + "\tisClosed:"+ el.isClosed+
                    "\n online:" + el.online

                    showText += "\n-----------------------\n"
                }
                myText.setText(showText)
                myAvatarView.setImageBitmap( getImageByUrl(usersArray[0].photo200Square, this@MainActivity) );
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

                        val newMesgsResult = ApiVK.getNewMessages()
                        numberOfRequest++
                        Log.d("ApiVK", "end of get new message request")

                        if (newMesgsResult.error != null) {
                            myText.setText("errr get new msgs:" + newMesgsResult.error)
                            return@thread
                        }

                        val newMessagesArray = newMesgsResult.result  as java.util.ArrayList<com.inplace.api.Message>

                        var messagesText = ""

                        for (el in newMessagesArray) {
                            messagesText += "message fromId:" + el.fromId + "\tdata:" + el.date +
                                    "\t isMymsg:" + el.myMsg + "\ntext:" + el.text
                            messagesText += "\n-----------------------\n"
                        }


                        var showText = "Номер запроса:" + numberOfRequest + "\tВаши новые сообещния:\n" +
                                messagesText

                        myText.setText(showText)
                        Thread.sleep(3000)
                    }
                }

            }
        }
        GetNewMsgSendMsg.setOnClickListener(GetNewMsgButtonClickListener)
    }

}





