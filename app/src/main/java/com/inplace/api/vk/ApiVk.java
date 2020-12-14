package com.inplace.api.vk;

import android.net.Uri;
import android.util.Log;
import com.inplace.api.CommandResult;
import com.inplace.models.Message;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.exceptions.VKApiException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ApiVk {

    public static final int MAX_ITEMS_COUNT = 200;


    // result -> VkUser
    public static CommandResult<? extends VkUser> getMe() {
        CommandResult<VkUser> result = new CommandResult<VkUser>();
        if (!VK.isLoggedIn()) {
            result.error = new Error("getMe():: no login before request");
            result.errTextMsg = "you need login";
            return result;
        }
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids.add(VK.getUserId());
        CommandResult<ArrayList<? extends VkUser>> resultUsers = getUsers(ids);
        result.result = resultUsers.result.get(0);
        result.error = resultUsers.error;
        result.errTextMsg = resultUsers.errTextMsg;
        return result;
    }




    // result -> null
    public static CommandResult<Boolean> logout(){
        VK.logout();
        return new CommandResult<Boolean>();
    }


    // result -> ArrayList<VkUser>
    public static CommandResult<ArrayList<? extends VkUser>> getUsers(List<Integer> idList) {

        CommandResult<ArrayList<? extends VkUser>> result = new CommandResult<>();

        if (!VK.isLoggedIn()){
            result.error = new Error("no session");
            result.errTextMsg = "you must login before this request";
            return result;
        }

        if (idList.size() > MAX_ITEMS_COUNT){
            result.error = new Error("max idList size is 200!");
            result.errTextMsg = "max idList size is 200!";
            return result;
        }

        int[] ids = new int[idList.size()];
        for (int i = 0; i < idList.size(); i++) {
            ids[i] = idList.get(i);
        }

        ArrayList<? extends VkUser> users = null;
        try {
            users = VK.executeSync(new GetUsersCommand(ids));
        } catch (InterruptedException e) {
            Log.e("vk SDK", "getUsers() InterruptedException:" + e);
            result.error = new Error("InterruptedException");
            result.errTextMsg = "InterruptedException";

        } catch (IOException e) {
            Log.e("vk SDK", "getUsers() IOException" + e);
            result.error = new Error("IOException");
            result.errTextMsg = "IOException";

        } catch (VKApiException e) {
            Log.e("vk SDK", "getUsers() VKApiException" + e);
            result.error = new Error("VKApiException");
            result.errTextMsg = "VKApiException";
        }
        catch (Exception e) {
            Log.e("vk SDK", "getUsers() Exception" + e);
            result.error = new Error("Some Exception");
            result.errTextMsg = "Some Exception";
        }

        if (users.size() < 1 ) {
            Log.e("vk SDK", "getUsers() users size < 1");
            result.error = new Error("users size < 1");
            result.errTextMsg = "users size < 1";
        }
        if (result.error != null)
            return result;

        result.result = users;
        return  result;

    }


    // result -> VkChatWithUsers{ArrayList<VkChat> chats; HashMap<Integer,VkUser> users}
    public static  CommandResult<VkChatWithUsers> getChats(int start, int end) {

        CommandResult<VkChatWithUsers> result = new CommandResult<>();

        if (!VK.isLoggedIn()){
            result.error = new Error("no session");
            result.errTextMsg = "you must login before this request";
            return result;
        }

        VkChatWithUsers chatsWithUsers = null;
        try {
            chatsWithUsers = VK.executeSync(new GetChatsCommand(start, end));
        } catch (InterruptedException e) {
            Log.e("vk SDK", "getChats() InterruptedException:" + e);
            result.error = new Error("InterruptedException");
            result.errTextMsg = "InterruptedException";

        } catch (IOException e) {
            Log.e("vk SDK", "getChats() IOException" + e);
            result.error = new Error("IOException");
            result.errTextMsg = "IOException";

        } catch (VKApiException e) {
            Log.e("vk SDK", "getChats() VKApiException" + e);
            result.error = new Error("VKApiException");
            result.errTextMsg = "VKApiException";
        }
        catch (Exception e) {
            Log.e("vk SDK", "getChats() Exception" + e);
            result.error = new Error("Some Exception");
            result.errTextMsg = "Some Exception";
        }

        if (result.error != null)
            return result;

        result.result = chatsWithUsers;
        return result;
    }


    // result -> ArrayList<Message>
    public static  CommandResult<ArrayList<? extends Message>> getMessages(int conversationId, int start, int end) {

        CommandResult<ArrayList<? extends Message>> result = new CommandResult<>();

        if (!VK.isLoggedIn()){
            result.error = new Error("no session");
            result.errTextMsg = "you must login before this request";
            return result;
        }

        ArrayList<? extends Message> messages = null;
        try {
            messages = VK.executeSync(new GetMessagesHistoryCommand(start, end, conversationId));
        } catch (InterruptedException e) {
            Log.e("vk SDK", "getMessages() InterruptedException:" + e);
            result.error = new Error("InterruptedException");
            result.errTextMsg = "InterruptedException";

        } catch (IOException e) {
            Log.e("vk SDK", "getMessages() IOException" + e);
            result.error = new Error("IOException");
            result.errTextMsg = "IOException";

        } catch (VKApiException e) {
            Log.e("vk SDK", "getMessages() VKApiException" + e);
            result.error = new Error("VKApiException");
            result.errTextMsg = "VKApiException";
        }
        catch (Exception e) {
            Log.e("vk SDK", "getMessages() Exception" + e);
            result.error = new Error("Some Exception");
            result.errTextMsg = "Some Exception";
        }

        if (result.error != null)
            return result;

        result.result = messages;
        return result;
    }

    /*
        Допустимые форматы: JPG, PNG, GIF.
        Ограничения: сумма высоты и ширины не более 14000px,
        файл объемом не более 50 МБ, соотношение сторон не менее 1:20.
     */

    // result -> Integer, new message id
    public static CommandResult<Integer> sendMessage(int userId, String msg, ArrayList<Uri> photosBitMaps) {

        CommandResult<Integer> result = new CommandResult<>();

        if (!VK.isLoggedIn()){
            result.error = new Error("no session");
            result.errTextMsg = "you must login before this request";
            return result;
        }

        if ( msg.equals("") && photosBitMaps.size() == 0){
            result.error = new Error("no msg and no photos");
            result.errTextMsg = "no msg and no photos";
            return result;
        }


        Integer sendMsgId = -1;
        try {

        // load photos and get photos urls
            ArrayList<ImageStruct> imageStructs = new ArrayList<ImageStruct>();
            if (photosBitMaps.size() > 0) {

                // check UploadServer
                if ( !VkSingleton.UploadServer.getIsInit() ) {
                    if ( !getUploadServer() ) {
                        result.error = new Error("no init UploadServer");
                        result.errTextMsg = "no init UploadServer";
                        return  result;
                    }
                }

                // load photos
                for (Uri photoUri : photosBitMaps) {
                    FileUploadInfo fileUploadInfo = VK.executeSync(new LoadPhotoCommand(photoUri));
                    ImageStruct imageStruct = VK.executeSync(new SaveImageCommand(fileUploadInfo));
                    imageStructs.add( imageStruct );
                }
            }

            // send message
            sendMsgId = VK.executeSync(new SendMessageCommand(userId, msg, imageStructs));

        } catch (InterruptedException e) {
            Log.e("vk SDK", "sendMessage() InterruptedException:" + e);
            result.error = new Error("InterruptedException");
            result.errTextMsg = "InterruptedException";

        } catch (IOException e) {
            Log.e("vk SDK", "sendMessage() IOException" + e);
            result.error = new Error("IOException");
            result.errTextMsg = "IOException";

        } catch (VKApiException e) {
            Log.e("vk SDK", "sendMessage() VKApiException" + e);
            result.error = new Error("VKApiException");
            result.errTextMsg = "VKApiException";
        }
        catch (Exception e) {
            Log.e("vk SDK", "sendMessage() Exception" + e);
            result.error = new Error("Some Exception");
            result.errTextMsg = "Some Exception";
        }

        if (result.error != null)
            return result;

        result.result = sendMsgId;
        return result;

    }



    // true -> is ok
    // false -> err or already init
    public static synchronized boolean getLongPollServer() {

        if (!VK.isLoggedIn()){
            return false;
        }

        if (VkSingleton.LongPollServer.getIsInit())
            return false;

        boolean isInited;
        try {
            isInited = VK.executeSync(new GetLongPullCommand());
        } catch (Exception e) {
            Log.e("vk SDK", "getLongPollServer() Exception" + e);
            return false;
        }
        return isInited;
    }


    // true -> is ok
    // false -> err or already init
    public static synchronized boolean getUploadServer() {

        if (!VK.isLoggedIn()){
            return false;
        }

        if (VkSingleton.UploadServer.getIsInit())
            return false;

        boolean isInited;
        try {
            isInited = VK.executeSync(new GetUploadServerCommand());
        } catch (Exception e) {
            Log.e("vk SDK", "getUploadServer() Exception" + e);
            return false;
        }
        return isInited;
    }




    // result -> ArrayList<Message>
    public static synchronized CommandResult<ArrayList<? extends Message>> getNewMessages() {

        CommandResult<ArrayList<? extends Message>> result = new CommandResult<>();

        if (!VK.isLoggedIn()){
            result.error = new Error("no session");
            result.errTextMsg = "you must login before this request";
            return result;
        }


        if ( !VkSingleton.LongPollServer.getIsInit() ) {
            if ( !getLongPollServer() ) {
                result.error = new Error("no init LongPollServer");
                result.errTextMsg = "no init LongPollServer";
                return  result;
            }
        }


        ArrayList<? extends Message> messages = null;
        try {
            messages = VK.executeSync(new GetNewMessagesCommand());
        } catch (InterruptedException e) {
            Log.e("vk SDK", "getNewMessages() InterruptedException:" + e);
            result.error = new Error("InterruptedException");
            result.errTextMsg = "InterruptedException";

        } catch (IOException e) {
            Log.e("vk SDK", "getNewMessages() IOException" + e);
            result.error = new Error("IOException");
            result.errTextMsg = "IOException";

        } catch (VKApiException e) {
            Log.e("vk SDK", "getNewMessages() VKApiException" + e);
            result.error = new Error("VKApiException");
            result.errTextMsg = "VKApiException";
        }
        catch (Exception e) {
            Log.e("vk SDK", "getNewMessages() Exception" + e);
            result.error = new Error("Some Exception");
            result.errTextMsg = "Some Exception";
        }

        if (result.error != null)
            return result;

        result.result = messages;
        return result;
    }



    }
