package com.inplace.api.telegram;

public interface LoginTelegramInterface  {

    public String getString(String msg);

    public void writeMsg(String msg);

    public String getDataDirPath();

    public void isLogin();

}