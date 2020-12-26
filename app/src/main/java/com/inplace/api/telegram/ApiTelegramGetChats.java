package com.inplace.api.telegram;

import android.util.Log;

import com.inplace.api.CommandResult;
import com.inplace.api.telegram.models.TelegramChat;
import com.inplace.api.telegram.models.TelegramUser;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;

public class ApiTelegramGetChats {


    public static ApiTelegramGetChats.GetChatsHandler getChatsHandler = null;

    public synchronized static CommandResult<ArrayList<TelegramChat>> getChats(){
        CommandResult<ArrayList<TelegramChat>> cr = new CommandResult<>();
        if (getChatsHandler == null) {
            getChatsHandler =  new ApiTelegramGetChats.GetChatsHandler();
        }
        if (TelegramSingleton.client == null) {
            Log.e("getMe","ERROR: TelegramSingleton.client == null, need init()");
            cr.error = new Error("TelegramSingleton.client == null, need init()");
            cr.errTextMsg = "u need login";
            return cr;
        }

        //ChatList chatList, long offsetOrder, long offsetChatId, int limit
        TelegramSingleton.client.send(new TdApi.GetChats(new TdApi.ChatListMain(), 2,2,300), getChatsHandler);
        return cr;
    }

    private static class GetChatsHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {

            switch (object.getConstructor()) {
                case TdApi.Chats.CONSTRUCTOR:

                    CommandResult<ArrayList<TelegramChat>> cr = new CommandResult<>();

                    TdApi.Chats chats = (TdApi.Chats) object;
                    Log.d("getChats", "object:" + object);


                    Log.d("getChats", "ids[0]:" + Long.toString(chats.chatIds[0]));

                    TelegramSingleton.activity.returnChats(cr);

                    break;
                default:
                    Log.e("GET CHATS:","ERROR:" + object.toString());

            }
        }
    }

}
