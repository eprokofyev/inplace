package com.inplace

import android.annotation.SuppressLint

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.inplace.api.CommandResult
import com.inplace.api.telegram.*
import com.inplace.api.telegram.models.TelegramChat
import com.inplace.api.telegram.models.TelegramUser
import java.util.ArrayList
import kotlin.concurrent.thread


class MainActivity  : AppCompatActivity(), LoginTelegramInterface  {

    var isSendBtnPressed = false


    val RESULT_LOAD_IMAGE = 55 // some code for pick images action

    var textID : Int = 0

    lateinit var imageView : ImageView

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
        // вызывается, даже если пользователь уже залогинен
        val buttonIDauth = resources.getIdentifier("authBtn", "id", packageName)
        val authBtn: Button = findViewById(buttonIDauth)
        val authBtnListen: View.OnClickListener = object : View.OnClickListener {

            override fun onClick(v: View?) {
                thread {
                    TelegramSingleton.logLvl = 1; // default = 0
                    ApiTelegramLogin.initTelegram(this@MainActivity);
                }
            }
        }
        authBtn.setOnClickListener(authBtnListen)


        // logout
        val buttonIDLogout = resources.getIdentifier("logoutBtn", "id", packageName)
        val myBtnLogout: Button = findViewById(buttonIDLogout)
        val logOutButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {

                thread {
                    ApiTelegramLogout.logout();
                }


            }
        }
        myBtnLogout.setOnClickListener(logOutButtonClickListener)



        // получения пользователя
        val buttonIDGetMe = resources.getIdentifier("getMe", "id", packageName)
        val myAvatarId = resources.getIdentifier("myAvatar", "id", packageName)
        val myAvatarView: ImageView = findViewById(myAvatarId)
        imageView = myAvatarView
        val myBtnGetMe: Button = findViewById(buttonIDGetMe)
        val GetMeButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                thread {
                    val cr = ApiTelegramGetMe.getMe();
                    if (cr.error != null) {
                        this@MainActivity.writeMsg("err get me call:" + cr.errTextMsg)
                    }
                }
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
                ApiTelegramGetChats.getChats();
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
                if (tUser != null) {

                    TelegramDownloader.getBitMapByTelegramId(tUser.avatarPhotoId);
                } else {
                    this@MainActivity.writeMsg("call get me first")
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




    }


    fun writeMsg(msg: String?) {
        val myText: TextView = findViewById(textID)
        myText.setText(msg)
    }

    override fun getDataDirPath(): String {
       return getApplicationContext().getFilesDir().getAbsolutePath();
    }


    // вызывается, даже если пользователь уже залогинен
    override fun isLogin(isLogin: Boolean) {
        val myText: TextView = findViewById(textID)
        if (isLogin) {
            myText.setText("успешная авторизация")
        } else {
            myText.setText("Ошибка авторизации, попробуйте снова")
        }
    }

    override fun getNumber(): String {

        this.writeMsg("Введите ваш номер в поле ввода и нажмите END INPUT")

        while(true) {
            if (isSendBtnPressed)
                break
            Thread.sleep(300)
            Log.d("getNumber()", "wait input")
        }
        isSendBtnPressed = false;

        val iDInput = resources.getIdentifier("msgToSend", "id", packageName)
        val inputForMsg: EditText = findViewById(iDInput)
        Log.d("getNumber()", "get input:" + inputForMsg)
        return inputForMsg.text.toString()
    }

    override fun getAuthCode(): String {
        this.writeMsg("Введите код из сообещения в Telegram")

        while(true) {
            if (isSendBtnPressed)
                break
            Thread.sleep(300)
            Log.d("getAuthCode()", "wait input")
        }
        isSendBtnPressed = false;

        val iDInput = resources.getIdentifier("msgToSend", "id", packageName)
        val inputForMsg: EditText = findViewById(iDInput)
        Log.d("getAuthCode()", "get input:" + inputForMsg)
        return inputForMsg.text.toString()
    }



    override fun logoutAct(result: CommandResult<Int>) {
        if (result.error == null && result.result == 0)
            this.writeMsg("Вы успешно вышли из телеги")
    }


    lateinit var tUser: TelegramUser

    override fun getMeAct(result: CommandResult<TelegramUser>?) {

        Log.d("getMeAct","call");
        if (result != null) {
            if (result.error == null) {
                val telegaUser: TelegramUser? = result.result
                if (telegaUser != null) {
                    this.writeMsg("getMeAct: id:" + telegaUser.id + "\nfirstName:"
                            + telegaUser.firstName + "   lastName:" + telegaUser.lastName
                            + "\n onlineTime:" + telegaUser.onlineTime
                            + "\n avatarPhotoId:" + telegaUser.avatarPhotoId
                            + "\n phoneNumber" + telegaUser.phoneNumber)
                    tUser = telegaUser;
                }
            } else {
                this.writeMsg("getMeAct() err:" + result.result.toString());

            }

        }
    }

    override fun setProfilePhoto(photoBitMap: Bitmap?) {
        imageView.setImageBitmap(photoBitMap)
    }

    override fun returnChats(result: CommandResult<ArrayList<TelegramChat>>?) {
        if (result != null) {
            if (result.error == null) {
                this.writeMsg("return chats")



            } else {
                this.writeMsg("err get chats:" + result.error)
            }
        }
    }


    override fun onDestroy() {
        // clear activity link
        ApiTelegramLogin.clear();
        super.onDestroy()
    }


}