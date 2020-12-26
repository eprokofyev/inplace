package com.inplace.api.telegram;

import android.util.Log;
import com.inplace.api.CommandResult;
import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;

public class ApiTelegramLogout {

    public static ApiTelegramLogout.LogoutHandler logoutHandler = null;

    public synchronized static void logout(){
        if (logoutHandler == null) {
            logoutHandler =  new ApiTelegramLogout.LogoutHandler();
        }
        if (TelegramSingleton.client == null) {

            Log.e("logout","ERROR: TelegramSingleton.client == null, need init()");
            return;
        }

        TelegramSingleton.canLogin = false;
        TelegramSingleton.alreadyShowLogin = false;

        TelegramSingleton.client.send(new TdApi.LogOut(), logoutHandler);

        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(500);
                TelegramSingleton.client.close();

                //
                String telegramDir = TelegramSingleton.activity.getDataDirPath() +
                        TelegramSingleton.telegramDir;

                File wallpaperDirectory = new File(telegramDir);
                wallpaperDirectory.delete();
                Log.d("logout ","delete is ok");

            } catch (Exception e) {
                Log.e("logout ","thread err:" + e);
            }
        });
        t1.start();

    }

    private static class LogoutHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {

            switch (object.getConstructor()) {
                case TdApi.UpdateAuthorizationState.CONSTRUCTOR: {
                    Log.d("LogoutHandler", "Logout:" + object.toString());
                    TelegramSingleton.client.close();
                    TelegramSingleton.authorizationState = null;
                    TelegramSingleton.haveAuthorization = false;
                    CommandResult<Integer> cr = new CommandResult<>();
                    cr.result = 0;
                    TelegramSingleton.activity.logoutAct(cr);
                    break;
                }
                default: {
                    Log.d("LogoutHandler:", "default state:" + object.toString());
                    TelegramSingleton.client.close();
                    TelegramSingleton.authorizationState = null;
                    TelegramSingleton.haveAuthorization = false;
                    CommandResult<Integer> cr = new CommandResult<>();
                    cr.result = 0;
                    TelegramSingleton.activity.logoutAct(cr);
                    TelegramSingleton.canQuit = false;
                }
            }

        }
    }





}
