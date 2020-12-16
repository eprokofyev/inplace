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
import com.inplace.api.telegram.ApiTelegram
import com.inplace.api.telegram.ApiTelegramGetMe
import com.inplace.api.telegram.LoginTelegramInterface
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import kotlin.concurrent.thread


class MainActivity  : AppCompatActivity(), LoginTelegramInterface  {

    var isSendBtnPressed = false


    val RESULT_LOAD_IMAGE = 55 // some code for pick images action

    var textID : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT > 9) {
            val policy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }




        textID = resources.getIdentifier("textId", "id", packageName)


        // авторизация
        val buttonIDauth = resources.getIdentifier("authBtn", "id", packageName)
        val authBtn: Button = findViewById(buttonIDauth)
        val authBtnListen: View.OnClickListener = object : View.OnClickListener {

            override fun onClick(v: View?) {


                ApiTelegram.initTelegram(this@MainActivity);

            }
        }
        authBtn.setOnClickListener(authBtnListen)




        // logout
        val buttonIDLogout = resources.getIdentifier("logoutBtn", "id", packageName)
        val myBtnLogout: Button = findViewById(buttonIDLogout)
        val logOutButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {




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

                ApiTelegramGetMe.getMe();

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

            }
        }
        myBtnGetChats.setOnClickListener(GetChatsButtonClickListener)



        // получение истории сообщений
        val buttonIDGetMsg = resources.getIdentifier("openLastChat", "id", packageName)
        val myBtnGetMsg: Button = findViewById(buttonIDGetMsg)
        val GetMsgsButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {


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
            isSendBtnPressed = true
            }
        }
        myBtnSendMsg.setOnClickListener(SendMsgsButtonClickListener)






        // Получение пользователей
        val buttonIDGetUsers = resources.getIdentifier("getUsersBtn", "id", packageName)
        val myBtnGetUsers: Button = findViewById(buttonIDGetUsers)
        val GetUsersButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {

            }
        }
        myBtnGetUsers.setOnClickListener(GetUsersButtonClickListener)







        // Подписка на новые сообщения
        val buttonIDGetNewMsg = resources.getIdentifier("waitMsg", "id", packageName)
        val GetNewMsgSendMsg: Button = findViewById(buttonIDGetNewMsg)

        var isStartedThread = false

        val GetNewMsgButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {



            }
        }
        GetNewMsgSendMsg.setOnClickListener(GetNewMsgButtonClickListener)






        val buttonShowImg = resources.getIdentifier("showAva", "id", packageName)
        val showImgBtn: Button = findViewById(buttonShowImg)


        val showImageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {

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








    }

    override fun getString(msg: String?): String {

        this.writeMsg(msg)

        while(true) {
            if (isSendBtnPressed)
                break
            Thread.sleep(300)
        }

        val iDInput = resources.getIdentifier("msgToSend", "id", packageName)
        val inputForMsg: EditText = findViewById(iDInput)
        return inputForMsg.text.toString()
    }

    override fun writeMsg(msg: String?) {
        val myText: TextView = findViewById(textID)
        myText.setText(msg)
    }

    override fun getDataDirPath(): String {
       return getApplicationContext().getFilesDir().getAbsolutePath();
    }


}