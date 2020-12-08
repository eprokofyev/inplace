package com.inplace.api.vk;

import android.util.Log;

import com.inplace.api.CommandResult;
import com.inplace.models.Message;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.exceptions.VKApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ApiVK {

    public static final int MAX_ITEMS_COUNT = 200;


    // result -> VkUser
    public static CommandResult<ArrayList<? extends VkUser>> getMeSKD() {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids.add(VK.getUserId());
        return getUsers(ids);
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


    // result -> Integer, new message id
    public static CommandResult<Integer> sendMessage(int userId, String msg) {

        CommandResult<Integer> result = new CommandResult<>();

        if (!VK.isLoggedIn()){
            result.error = new Error("no session");
            result.errTextMsg = "you must login before this request";
            return result;
        }

        Integer sendMsgId = -1;
        try {
            sendMsgId = VK.executeSync(new SendMessageCommand(userId, msg));
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

        boolean isIgnited;
        try {
            isIgnited = VK.executeSync(new GetLongPullCommand());
        } catch (Exception e) {
            Log.e("vk SDK", "getLongPollServer() Exception" + e);
            return false;
        }
        return isIgnited;
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
