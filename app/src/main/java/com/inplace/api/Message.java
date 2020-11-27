package com.inplace.api;

public class Message {

    public int date = -1;
    public String text = "";

    public int fromId = -1;
    public boolean myMsg = false;

    public int messageId = -1;

    // 1 -> VK
    // 2 -> telegram
    public int fromMessenger = -1;

}
