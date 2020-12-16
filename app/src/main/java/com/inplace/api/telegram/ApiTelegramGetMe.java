package com.inplace.api.telegram;

import android.util.Log;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public class ApiTelegramGetMe {

    public static GetMeHandler getMeHandler = null;
    
    public synchronized static void getMe(){
        if (getMeHandler == null) {
            getMeHandler =  new GetMeHandler();
        }
        TelegramSingleton.client.send(new TdApi.GetMe(), getMeHandler);
    }

    private static class GetMeHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            Log.e("GET ME:",object.toString());
        }
    }

}
