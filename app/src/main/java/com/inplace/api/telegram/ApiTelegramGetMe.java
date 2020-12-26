package com.inplace.api.telegram;

import android.util.Log;

import com.inplace.api.CommandResult;
import com.inplace.api.telegram.models.TelegramUser;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public class ApiTelegramGetMe {

    public static GetMeHandler getMeHandler = null;

    public synchronized static  CommandResult<Integer> getMe(){
        CommandResult<Integer> cr = new CommandResult<>();
        if (getMeHandler == null) {
            getMeHandler =  new GetMeHandler();
        }
        if (TelegramSingleton.client == null) {
            Log.e("getMe","ERROR: TelegramSingleton.client == null, need init()");
            cr.error = new Error("TelegramSingleton.client == null, need init()");
            cr.errTextMsg = "u need login";
            return cr;
        }

        TelegramSingleton.client.send(new TdApi.GetMe(), getMeHandler);
        return cr;
    }

    private static class GetMeHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {

            switch (object.getConstructor()) {
                case TdApi.User.CONSTRUCTOR:

                    CommandResult<TelegramUser> cr = new CommandResult<>();
                    TelegramUser user = new TelegramUser();

                    TdApi.User tdUser = (TdApi.User) object;
                    user.firstName = tdUser.firstName;
                    user.lastName = tdUser.lastName;
                    user.id = tdUser.id;
                    user.phoneNumber = tdUser.phoneNumber;
                    user.haveAccess = tdUser.haveAccess;

                    TdApi.UserStatusOnline onlineStatus = (TdApi.UserStatusOnline) tdUser.status;
                    user.onlineTime = onlineStatus.expires;

                    if (tdUser.profilePhoto != null) {
                        TdApi.ProfilePhoto profilePhoto = tdUser.profilePhoto;
                        user.avatarPhotoId = profilePhoto.small.id;
                    }

                    cr.result = user;
                    TelegramSingleton.activity.getMeAct(cr);
                    Log.d("GetMeHandler GET ME:", object.toString());

                    break;
                default:
                    Log.e("GET ME:","ERROR:" + object.toString());

            }
        }
    }

}
