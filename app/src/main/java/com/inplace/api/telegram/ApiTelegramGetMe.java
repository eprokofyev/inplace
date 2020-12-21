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
        if (TelegramSingleton.client == null) {
            Log.e("getMe","ERROR: TelegramSingleton.client == null, need init()");
            return;
        }


        //public DownloadFile(int fileId, int priority, int offset, int limit, boolean synchronous)
        //TdApi.DownloadFile fd = new TdApi.DownloadFile(11111, 1, 0, 0, true);


        TelegramSingleton.client.send(new TdApi.GetMe(), getMeHandler);
    }

    private static class GetMeHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {

            switch (object.getConstructor()) {
                case TdApi.User.CONSTRUCTOR:
                    Log.d("GET ME:", object.toString());
                    break;
                default:
                    Log.e("GET ME:","ERROR:" + object.toString());

            }

        }
    }

}
