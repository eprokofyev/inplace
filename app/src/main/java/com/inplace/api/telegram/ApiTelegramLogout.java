package com.inplace.api.telegram;

import android.util.Log;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public class ApiTelegramLogout {

    public static ApiTelegramLogout.LogoutHandler logoutHandler = null;

    public synchronized static void logout(){
        if (logoutHandler == null) {
            logoutHandler =  new ApiTelegramLogout.LogoutHandler();
        }
        if (TelegramSingleton.client == null) {
            Log.e("getMe","ERROR: TelegramSingleton.client == null, need init()");
            return;
        }

        TelegramSingleton.client.send(new TdApi.LogOut(), logoutHandler);
    }

    private static class LogoutHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {

            switch (object.getConstructor()) {
                case TdApi.LogOut.CONSTRUCTOR:
                    Log.d("Logout:", object.toString());
                    break;
                default:
                    Log.e("Logout:","ERROR:" + object.toString());
            }

            TelegramSingleton.client.close();
        }
    }





}
