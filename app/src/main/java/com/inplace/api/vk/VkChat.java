package com.inplace.api.vk;

public class VkChat {

    public static final int CHAT_TYPE_USER = 1;
    public static final int CHAT_TYPE_GROUP_CHAT = 2;

    // 1 - user
    // 2 - group chat
    // 3 - public group // todo not work now
    public int chatType = -1;
    public String groupChatTitle = "";

    public Long date = -1L;
    public String text = "";
    public int chatWithId = -1;
    public int lastMsgFromId = -1;
    public int lasMsgId = -1;

}
