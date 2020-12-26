package com.inplace.api.telegram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;


public class TelegramDownloader {


    private static FileHandler fileHandler = null;

    public static void getBitMapByTelegramId(int fileId){
        fileHandler = new FileHandler();
        TelegramSingleton.client.send(new TdApi.DownloadFile(fileId, 32, 0,0,true), fileHandler);
        TelegramSingleton.client.send(new TdApi.DownloadFile(fileId, 32, 0,0,true), fileHandler);
    }



    private static class FileHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {

            Log.d("TelegramDownloader", "obj:" + object.toString());

            switch (object.getConstructor()) {
                case TdApi.File.CONSTRUCTOR:
                    TdApi.File file = (TdApi.File) object;
                    // TODO check file exist
                    Bitmap bitmap = BitmapFactory.decodeFile(file.local.path);
                    TelegramSingleton.activity.setProfilePhoto(bitmap);
                    break;
                default:
                    Log.e("FileHandler:", "default state:" + object.toString());
            }

        }
    }

}
