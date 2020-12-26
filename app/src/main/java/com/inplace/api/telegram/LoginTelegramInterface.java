package com.inplace.api.telegram;

import android.graphics.Bitmap;

import com.inplace.api.CommandResult;
import com.inplace.api.telegram.models.TelegramChat;
import com.inplace.api.telegram.models.TelegramUser;

import java.util.ArrayList;

public interface LoginTelegramInterface  {

//    public String getString(String msg);
//
//    public void writeMsg(String msg);

    public String getDataDirPath();



    // login
    public String getNumber();
    public String getAuthCode();
    public void isLogin(Boolean isLogin);


    // logout
    public void logoutAct(CommandResult<Integer> result);


    // get me
    public void getMeAct(CommandResult<TelegramUser> result);


    // using after load photo
    public void setProfilePhoto(Bitmap photoBitMap);


    public void returnChats(CommandResult<ArrayList<TelegramChat>> result);

}