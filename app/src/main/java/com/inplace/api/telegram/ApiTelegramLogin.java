package com.inplace.api.telegram;

import android.util.Log;

import com.inplace.MainActivity;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public class ApiTelegramLogin {

    private static MainActivity loginActivity = null;

    public static void clear() {loginActivity = null;}


    public static void initTelegram(MainActivity loginActivity) throws InterruptedException {

        ApiTelegramLogin.loginActivity = loginActivity;

        Client.execute(new TdApi.SetLogVerbosityLevel(3));

//        if (Client.execute(new TdApi.SetLogStream(new TdApi.LogStreamFile("tdlib.log", 1 << 27, false))) instanceof TdApi.Error) {
//            throw new IOError(new IOException("Write access to the current directory is required"));
//        }

        // create client
        TelegramSingleton.client = Client.create(new ApiTelegram.UpdateHandler(), null, null);

        // test Client.execute
//        TestHandler defaultHandler = new TestHandler();
//        defaultHandler.onResult(Client.execute(new TdApi.GetTextEntities("@telegram /test_command https://telegram.org telegram.me @gif @test")));
    }




    public static void onAuthorizationStateUpdated(TdApi.AuthorizationState authorizationState) {
        if (authorizationState != null) {
            TelegramSingleton.authorizationState = authorizationState;
        }
        switch (authorizationState.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
                parameters.databaseDirectory = loginActivity.getDataDirPath();
                parameters.useMessageDatabase = true;
                parameters.useSecretChats = true;
                parameters.apiId = 2036243;
                parameters.apiHash = "78ea6c32edb31e09fadc192705b6fc3e";
                parameters.systemLanguageCode = "en";
                parameters.deviceModel = "mobile";
                parameters.applicationVersion = "1.0";
                parameters.enableStorageOptimizer = true;

                TelegramSingleton.client.send(new TdApi.SetTdlibParameters(parameters), new ApiTelegramLogin.AuthorizationRequestHandler());
                break;
            case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                TelegramSingleton.client.send(new TdApi.CheckDatabaseEncryptionKey(), new ApiTelegramLogin.AuthorizationRequestHandler());
                break;
            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR: {
                String phoneNumber = loginActivity.getString("Please enter phone number: ");
                TelegramSingleton.client.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, null), new ApiTelegramLogin.AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitOtherDeviceConfirmation.CONSTRUCTOR: {
                String link = ((TdApi.AuthorizationStateWaitOtherDeviceConfirmation) authorizationState).link;
                Log.d("Api telega","Please confirm this login link on another device: " + link);
                break;
            }
            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR: {
                String code = loginActivity.getString("Please enter authentication code: ");
                Log.d("TdApi", "send code!");
                TelegramSingleton.client.send(new TdApi.CheckAuthenticationCode(code), new ApiTelegramLogin.AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitRegistration.CONSTRUCTOR: {
                String firstName = loginActivity.getString("Please enter your first name: ");
                String lastName = loginActivity.getString("Please enter your last name: ");
                TelegramSingleton.client.send(new TdApi.RegisterUser(firstName, lastName), new ApiTelegramLogin.AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR: {
                String password = loginActivity.getString("Please enter password: ");
                TelegramSingleton.client.send(new TdApi.CheckAuthenticationPassword(password), new ApiTelegramLogin.AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                TelegramSingleton.haveAuthorization = true;
                TelegramSingleton.authorizationLock.lock();
                try {
                    TelegramSingleton.gotAuthorization.signal();
                } finally {
                    TelegramSingleton.authorizationLock.unlock();
                }
                break;
            case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
                TelegramSingleton.haveAuthorization = false;
                loginActivity.writeMsg("Logging out");
                break;
            case TdApi.AuthorizationStateClosing.CONSTRUCTOR:
                TelegramSingleton.haveAuthorization = false;
                loginActivity.writeMsg("Closing");
                break;
            case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
                loginActivity.writeMsg("Closed");
                if (!TelegramSingleton.needQuit) {
                    TelegramSingleton.client = Client.create(new ApiTelegram.UpdateHandler(), null, null); // recreate client after previous has closed
                } else {
                    TelegramSingleton.canQuit = true;
                }
                break;
            default:
                Log.d("Api telega","Unsupported authorization state:"  + authorizationState);
        }
    }



    private static class AuthorizationRequestHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.Error.CONSTRUCTOR:
                    Log.d("Api telega","Receive an error:"  + object);
                    onAuthorizationStateUpdated(null); // repeat last action
                    break;
                case TdApi.Ok.CONSTRUCTOR:
//                    if (TdApi.GetPhoneNumberInfo())
//                    loginActivity.isLogin();
                    break;
                default:
                    Log.d("Api telega","Receive wrong response from TDLib:"  + object);
            }
        }
    }

    public static class TestHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            Log.d("default handler:", object.toString());
            loginActivity.writeMsg("test handler result:\n" + object.toString());
        }
    }


}
