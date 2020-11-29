package com.inplace.api.vk;

import android.util.Log;

import com.inplace.api.CommandResult;
import com.inplace.api.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiVK {

    private static final  String API_VERSION = "5.126";

    private static final String API_HOST_AUTH = "https://oauth.vk.com/";
    private static final String API_HOST = "https://api.vk.com/";

    private static final String CLIENT_ID = "2685278";
    private static final String CLIENT_SECRET = "lxhD8OD7dMsqtXIm5IUY";

    private static final String URL_AUTH = API_HOST_AUTH +"token?grant_type=password&client_id="
            + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&display=mobile";

    // todo make lazy
    private static final OkHttpClient httpClient =  new OkHttpClient();

    private static final int MAX_REQUEST_ITEM_COUNT = 200;

    private static final int START_ID_GROUP_CHAT = 2000000000;


    private static class LongPollServer {

        public static boolean isInit = false;
        public static String server = "";
        public static String key = "";
        public static int ts = -1;
        public static int pts = -1;

        public static synchronized void setInit(String serverValue, String keyValue, int tsValue) {
            server = serverValue;
            key = keyValue;
            ts = tsValue;
            isInit = true;
        }

        public static boolean getIsInit(){
            return  isInit;
        }

    }

    private static final int START_RANDOM_ID = 100000;
    private static final int RANDOM_ID_DELTA = 10000;


    private static int randomIdStart = ThreadLocalRandom.current().
            nextInt(START_RANDOM_ID + RANDOM_ID_DELTA, Integer.MAX_VALUE - START_RANDOM_ID);


    private static synchronized int getNextNumber() {
        return ++randomIdStart;
    }




    // result -> Integer, user id
    // bad login -> result.errTextMsg = "bad login-pass";
    public static CommandResult login(String name, String pass) {

        CommandResult result = new CommandResult();

        if ( !(VkSingleton.getAccessToken().equals("") || VkSingleton.getUserId() == -1) ) {
            Log.d("ApiVK", "already login! id:" + VkSingleton.getUserId());
            result.error = new Error("already login!");
            result.errTextMsg = "you already have session, for relogin u need logout";
            return result;
        }

        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(URL_AUTH)).newBuilder();
        httpBuilder.addQueryParameter("username", name);
        httpBuilder.addQueryParameter("password", pass);
        httpBuilder.addQueryParameter("response_type", "code");
        httpBuilder.addQueryParameter("password", pass);

        Request request = new Request.Builder().url(httpBuilder.build()).build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.body() == null) {
                result.error = new Error("response body is null");
                result.errTextMsg = "err read response body";
                return  result;
            }
            String body = response.body().string();
            JSONObject mainObject = new JSONObject(body);

            String  access_token = mainObject.getString("access_token");
            int userId = Integer.parseInt(mainObject.getString("user_id"));

            VkSingleton.setAccessToken(access_token);
            VkSingleton.setUserId(userId);

            result.result = userId;

            return result;

        }
        catch (IOException e) {
            Log.e("ApiVK", "error in getting response get request okhttp");
            result.error = new Error("error in getting response get request okhttp");
            result.errTextMsg = "error in getting response get request okhttp";
        }
        catch (JSONException e){
            Log.e("ApiVK", "error in JSONObject or bad login-pass", e);
            result.error = new Error("bad login-pass");
            result.errTextMsg = "bad login-pass";
        }
        catch (NumberFormatException e){
            Log.e("ApiVK", "error in Integer.parseInt", e);
            result.error = new Error("error in Integer.parseInt");
            result.errTextMsg = "error in Integer.parseInt";
        }
        catch (Exception e){
            Log.e("ApiVK", "some err", e);
            result.error = new Error("some err");
            result.errTextMsg = "some err";
        }
        return result;
    }


    // result -> null
    public static CommandResult logout(){
        VkSingleton.setAccessToken("");
        VkSingleton.setUserId(-1);
        return new CommandResult();
    }


    // result -> VkUser
    public static CommandResult getMe() {

        CommandResult result = new CommandResult();

        if (VkSingleton.getAccessToken().equals("") || VkSingleton.getUserId() == -1){
            result.error = new Error("no session");
            result.errTextMsg = "you must login before this request";
            return result;
        }

        final String method = "/method/users.get";
        final String fields = "about,status,online,photo_200";

        HttpUrl.Builder httpBuider = Objects.requireNonNull(HttpUrl.parse(API_HOST + method))
                                     .newBuilder();

        httpBuider.addQueryParameter("access_token", VkSingleton.getAccessToken());
        httpBuider.addQueryParameter("user_ids", String.valueOf(VkSingleton.getUserId()));
        httpBuider.addQueryParameter("v", API_VERSION);
        httpBuider.addQueryParameter("fields", fields);

        Request request = new Request.Builder().url(httpBuider.build()).build();

        Response response = null;
        try {

            response = httpClient.newCall(request).execute();
            if (response.body() == null) {
                result.error = new Error("response body is null");
                result.errTextMsg = "err read response body";
                return result;
            }
            String body = response.body().string();

            JSONObject mainObject = new JSONObject(body);
            mainObject = mainObject.getJSONArray("response").getJSONObject(0);

            VkUser vkUser = new VkUser();

            vkUser.firstName = mainObject.getString("first_name");
            vkUser.lastName = mainObject.getString("last_name");
            String is_closed = mainObject.getString("is_closed");
            vkUser.about = mainObject.getString("about");
            vkUser.status = mainObject.getString("status");
            String online = mainObject.getString("online");
            vkUser.photo200Square = mainObject.getString("photo_200");

            vkUser.isClosed = is_closed.equals("true");
            vkUser.online = online.equals("1");

            vkUser.id = VkSingleton.getUserId();

            result.result = vkUser;

            return result;

    }
    catch (IOException e) {
        Log.e("ApiVK", "error in getting response get request okhttp");
        result.error = new Error("error in getting response get request okhttp");
        result.errTextMsg = "error in getting response get request okhttp";
    }
    catch (JSONException e){
        Log.e("ApiVK", "error in parse json", e);
        result.error = new Error("error in parse json");
        result.errTextMsg = "error in parse json";
    }
    catch (Exception e) {
        result.error = new Error("some err in get user" + e);
        result.errTextMsg = "some err in get user";
        Log.e("ApiVK", "some err in get user" + e);
    }

    return result;
    }


    // result -> ArrayList<VkChat>
    public static  CommandResult getChats(int start, int end) {

        CommandResult result = new CommandResult();

        if (VkSingleton.getAccessToken().equals("") || VkSingleton.getUserId() == -1) {
            result.error = new Error("no session");
            result.errTextMsg = "you must login before this request";
            return result;
        }

        final String method = "method/messages.getConversations";

        // todo not work =(
        //final String fields = "profiles,about,status,online,last_name,first_name,online";

        int count = end - start;

        if (count < 0 || count > MAX_REQUEST_ITEM_COUNT) {
            result.error = new Error("bad chat count, must be (0,200]");
            result.errTextMsg = "bad chat count, must be (0,200], count = count = end - start";
            return result;
        }

        HttpUrl.Builder httpBuider = Objects.requireNonNull(HttpUrl.parse(API_HOST + method))
                .newBuilder();

        httpBuider.addQueryParameter("access_token", VkSingleton.getAccessToken());
        httpBuider.addQueryParameter("v", API_VERSION);
        httpBuider.addQueryParameter("offset", String.valueOf(start));
        httpBuider.addQueryParameter("count", String.valueOf(count));
        httpBuider.addQueryParameter("extended", "1");
        //httpBuider.addQueryParameter("fields", fields);

        Request request = new Request.Builder().url(httpBuider.build()).build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.body() == null) {
                result.error = new Error("response body is null");
                result.errTextMsg = "err read response body";
                return result;
            }
            String body = response.body().string();

            JSONObject mainObject = new JSONObject(body);
            JSONArray jsonConversations = mainObject.getJSONObject("response").getJSONArray("items");


            ArrayList<VkChat> chatList =  new ArrayList<VkChat>();


            for (int i = 0; i < jsonConversations.length(); i++) {

                VkChat vkChat = new VkChat();

                String type = jsonConversations.getJSONObject(i).getJSONObject("conversation").getJSONObject("peer").getString("type");
                vkChat.chatWithId = Integer.parseInt(jsonConversations.getJSONObject(i).getJSONObject("conversation").getJSONObject("peer").getString("id"));
                Log.d("ApiVK get chats:", "type:" + type);


                JSONObject lastMessageObj = jsonConversations.getJSONObject(i).getJSONObject("last_message");
                vkChat.text = lastMessageObj.getString("text");
                vkChat.date = Integer.parseInt(lastMessageObj.getString("date"));
                vkChat.lastMsgFromId = Integer.parseInt(lastMessageObj.getString("from_id"));
                vkChat.lasMsgId = Integer.parseInt(lastMessageObj.getString("id"));

                if (type.equals("user")) {
                    vkChat.chatType = VkChat.CHAT_TYPE_USER;



                }
                if (type.equals("chat")) {
                    vkChat.chatType = VkChat.CHAT_TYPE_GROUP_CHAT;
                }
                // todo add here public group

                chatList.add(vkChat);
            }

            result.result = chatList;

            return result;

        }
        catch (IOException e) {
            Log.e("ApiVK", "error in getting response get request okhttp");
            result.error = new Error("error in getting response get request okhttp");
            result.errTextMsg = "error in getting response get request okhttp";
        }
        catch (JSONException e){
            Log.e("ApiVK", "error in JSONObject", e);
            result.error = new Error("error in JSONObject");
            result.errTextMsg = "error in JSONObject";
        }
        catch (NumberFormatException e){
            Log.e("ApiVK", "error in Integer.parseInt", e);
            result.error = new Error("error in Integer.parseInt");
            result.errTextMsg = "error in Integer.parseInt";
        }
        catch (Exception e){
            Log.e("ApiVK", "some err", e);
            result.error = new Error("some err");
            result.errTextMsg = "some err";
        }
        return result;
    }


    // result -> ArrayList<Message>
    public static  CommandResult getMessages(int conversationId, int start, int end) {

        CommandResult result = new CommandResult();

        if (VkSingleton.getAccessToken().equals("") || VkSingleton.getUserId() == -1){
            result.error = new Error("no session");
            result.errTextMsg = "you must login before this request";
            return result;
        }

        int count = end-start;
        if (count < 0 || count > MAX_REQUEST_ITEM_COUNT) {
            result.error = new Error("bad messages count, must be (0,200]");
            result.errTextMsg = "bad messages count, must be (0,200], count = count = end - start";
            return result;
        }


        final String method = "/method/messages.getHistory";
        final String fields = "about,status,online,photo_200";

        HttpUrl.Builder httpBuider = Objects.requireNonNull(HttpUrl.parse(API_HOST + method))
                .newBuilder();

        httpBuider.addQueryParameter("access_token", VkSingleton.getAccessToken());
        httpBuider.addQueryParameter("v", API_VERSION);
        httpBuider.addQueryParameter("offset", String.valueOf(start));
        httpBuider.addQueryParameter("count", String.valueOf(count));
        httpBuider.addQueryParameter("user_id", String.valueOf(conversationId));
        httpBuider.addQueryParameter("peer_id", String.valueOf(conversationId));
        httpBuider.addQueryParameter("fields", fields);
        httpBuider.addQueryParameter("extended", "1");

        Request request = new Request.Builder().url(httpBuider.build()).build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.body() == null) {
                result.error = new Error("response body is null");
                result.errTextMsg = "err read response body";
                return result;
            }
            String body = response.body().string();
            Log.d("ApiVK get messages:", "body:"+ body);


            JSONObject mainObject = new JSONObject(body);
            JSONArray jsonMessages = mainObject.getJSONObject("response").getJSONArray("items");


            ArrayList<Message> messageList =  new ArrayList<Message>();

            for (int i = 0; i < jsonMessages.length(); i++) {

                Message oneMessage = new Message();

                JSONObject OneMsgJsonObj = jsonMessages.getJSONObject(i);

                oneMessage.text = OneMsgJsonObj.getString("text");

                // add only text msg
                if (oneMessage.text.equals("")) {
                    continue;
                }

                oneMessage.fromId = Integer.parseInt(OneMsgJsonObj.getString("from_id"));
                oneMessage.date = Integer.parseInt(OneMsgJsonObj.getString("date"));

                if (oneMessage.fromId == VkSingleton.getUserId()){
                    oneMessage.myMsg = true;
                }

                oneMessage.messageId = Integer.parseInt(OneMsgJsonObj.getString("id"));

                oneMessage.fromMessenger = 1; // todo replace enum

                messageList.add(oneMessage);
            }
            result.result = messageList;
            return result;

        }
        catch (IOException e) {
            Log.e("ApiVK", "error in getting response get request okhttp");
            result.error = new Error("error in getting response get request okhttp");
            result.errTextMsg = "error in getting response get request okhttp";
        }
        catch (JSONException e){
            Log.e("ApiVK", "error parse Json:", e);
            result.error = new Error("error parse Json" + e);
            result.errTextMsg = "error parse Json";
        }
        catch (NumberFormatException e){
            Log.e("ApiVK", "error in Integer.parseInt", e);
            result.error = new Error("error in Integer.parseInt" + e);
            result.errTextMsg = "error in Integer.parseInt";
        }
        catch (Exception e){
            Log.e("ApiVK", "some err", e);
            result.error = new Error("some err");
            result.errTextMsg = "some err";
        }
        return result;
    }



    // result -> ArrayList<VkUser>
    public static CommandResult getUsers(ArrayList<Integer> idList) {

        CommandResult result = new CommandResult();

        if (VkSingleton.getAccessToken().equals("") || VkSingleton.getUserId() == -1){
            result.error = new Error("no session");
            result.errTextMsg = "you must login before this request";
            return  result;
        }

        if (idList.size() == 0) {
            result.error = new Error("no ids");
            result.errTextMsg = "ArrayList<Integer> idList size == 0";
            return result;
        }

        final String method = "/method/users.get";
        final String fields = "about,status,online,photo_200";

        StringBuilder usersIds = new StringBuilder();

        for (int i = 0 ; i < idList.size() ; i++) {
            usersIds.append(idList.get(i));
            if (i + 1 != idList.size()) {
                usersIds.append(",");
            }
        }

        // todo ids to POST PARAMETERS

        HttpUrl.Builder httpBuider = Objects.requireNonNull(HttpUrl.parse(API_HOST + method))
                .newBuilder();

        httpBuider.addQueryParameter("access_token", VkSingleton.getAccessToken());
        httpBuider.addQueryParameter("v", API_VERSION);
        httpBuider.addQueryParameter("user_ids", usersIds.toString());
        httpBuider.addQueryParameter("fields", fields);
        httpBuider.addQueryParameter("extended", "1");

        Request request = new Request.Builder().url(httpBuider.build()).build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.body() == null) {
                result.error = new Error("response body is null");
                result.errTextMsg = "err read response body";
                return result;
            }
            String body = response.body().string();

            JSONObject mainObject = new JSONObject(body);
            JSONArray jsonUsers = mainObject.getJSONArray("response");

            ArrayList<VkUser> userList =  new ArrayList<VkUser>();

            for (int i = 0; i < jsonUsers.length(); i++) {

                VkUser vkUser = new VkUser();

                JSONObject oneUserJsonObj = jsonUsers.getJSONObject(i);


                vkUser.id = Integer.parseInt(oneUserJsonObj.getString("id"));

                // skip group chat
                if (vkUser.id > START_ID_GROUP_CHAT)
                    continue;

                vkUser.firstName = oneUserJsonObj.getString("first_name");
                vkUser.lastName = oneUserJsonObj.getString("last_name");
                String is_closed = oneUserJsonObj.getString("is_closed");

                vkUser.isClosed = is_closed.equals("true");


//                if (!is_closed.equals("true"))
//                    vkUser.about = oneUserJsonObj.getString("about");

                vkUser.status = oneUserJsonObj.getString("status");
                String online = oneUserJsonObj.getString("online");
                vkUser.online = online.equals("1");
                vkUser.photo200Square = oneUserJsonObj.getString("photo_200");

                userList.add(vkUser);
            }

            result.result = userList;

            return result;

        }
        catch (IOException e){
            Log.e("ApiVK", "error in getting response get request okhttp");
            result.error = new Error("error in getting response get request okhttp");
            result.errTextMsg = "error in getting response get request okhttp";
        }
        catch (JSONException e){
            Log.e("ApiVK", "error parse Json:", e);
            result.error = new Error("error parse Json" + e);
            result.errTextMsg = "error parse Json";
        }
        catch (NumberFormatException e){
            Log.e("ApiVK", "error in Integer.parseInt", e);
            result.error = new Error("error in Integer.parseInt" + e);
            result.errTextMsg = "error in Integer.parseInt";
        }
        catch (Exception e){
            Log.e("ApiVK", "some err", e);
            result.error = new Error("some err");
            result.errTextMsg = "some err";
        }

        return result;
    }


    // result -> Integer, new message id
    public static CommandResult sendMessage(int userId, String msg) {

        CommandResult result = new CommandResult();

        if (userId < 0) {
            result.error = new Error("bad id");
            result.errTextMsg = "id must be  > 0";
            return  result;
        }

        if (VkSingleton.getAccessToken().equals("") || VkSingleton.getUserId() == -1){
            result.error = new Error("no session");
            result.errTextMsg = "you must login before this request";
            return  result;
        }

        final String method = "/method/messages.send";

        int random_id = getNextNumber();

        HttpUrl.Builder httpBuider = Objects.requireNonNull(HttpUrl.parse(API_HOST + method))
                .newBuilder();

        httpBuider.addQueryParameter("access_token", VkSingleton.getAccessToken());
        httpBuider.addQueryParameter("v", API_VERSION);
        httpBuider.addQueryParameter("user_id", String.valueOf(userId));
        httpBuider.addQueryParameter("random_id", String.valueOf(random_id));
        httpBuider.addQueryParameter("peer_id", String.valueOf(userId));
        httpBuider.addQueryParameter("message", msg);

        Request request = new Request.Builder().url(httpBuider.build()).build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.body() == null) {
                result.error = new Error("response body is null");
                result.errTextMsg = "err read response body";
                return result;
            }
            String body = response.body().string();
            Log.d("ApiVK get messages:", "body:"+ body);

            JSONObject mainObject = new JSONObject(body);
            String messageIdString = mainObject.getString("response");

            result.result = Integer.parseInt(messageIdString);

            return result;

        }
        catch (IOException e){
            Log.e("ApiVK", "error in getting response get request okhttp");
            result.error = new Error("error in getting response get request okhttp");
            result.errTextMsg = "error in getting response get request okhttp";
        }
        catch (JSONException e){
            Log.e("ApiVK", "error parse Json:", e);
            result.error = new Error("error parse Json" + e);
            result.errTextMsg = "error parse Json";
        }
        catch (NumberFormatException e){
            Log.e("ApiVK", "error in Integer.parseInt", e);
            result.error = new Error("error in Integer.parseInt" + e);
            result.errTextMsg = "error in Integer.parseInt";
        }
        catch (Exception e){
            Log.e("ApiVK", "some err", e);
            result.error = new Error("some err");
            result.errTextMsg = "some err";
        }

        return result;
    }



    // true -> is ok
    public static synchronized boolean getLongPollServer() {

        if (VkSingleton.getAccessToken().equals("") || VkSingleton.getUserId() == -1){
            return false;
        }

        if (LongPollServer.getIsInit())
            return false;

        final String method = "/method/messages.getLongPollServer";

        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(API_HOST + method))
                .newBuilder();

        httpBuilder.addQueryParameter("access_token", VkSingleton.getAccessToken());
        httpBuilder.addQueryParameter("v", API_VERSION);
        httpBuilder.addQueryParameter("need_pts", "1");
        httpBuilder.addQueryParameter("lp_version", "3");


        Request request = new Request.Builder().url(httpBuilder.build()).build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.body() == null) {
                return false;
            }

            String body = response.body().string();
            Log.d("ApiVK LongPollServer:", "body:"+ body);

            JSONObject mainObject = new JSONObject(body);
            JSONObject messageIdString = mainObject.getJSONObject("response");

            String server = messageIdString.getString("server");
            String key = messageIdString.getString("key");
            int ts = Integer.parseInt(messageIdString.getString("ts"));

            LongPollServer.setInit(server, key, ts);

            return true;

        } catch (Exception e) {
            Log.e("ApiVK", "error get getLongPollServer" + e);
        }

        return false;
    }




    // result -> ArrayList<Message>
    public static synchronized CommandResult getNewMessages() {

        CommandResult result = new CommandResult();

        if ( !LongPollServer.getIsInit() ) {
            if ( !getLongPollServer() ) {
                result.error = new Error("no init LongPollServer");
                result.errTextMsg = "no init LongPollServer";
                return  result;
            }
        }


        if (VkSingleton.getAccessToken().equals("") || VkSingleton.getUserId() == -1){
            result.error = new Error("no session");
            result.errTextMsg = "you must login before this request";
            return  result;
        }

        final String method = "/method/messages.getLongPollHistory";

        HttpUrl.Builder newBuilder = Objects.requireNonNull(HttpUrl.parse(API_HOST + method))
                .newBuilder();

        newBuilder.addQueryParameter("access_token", VkSingleton.getAccessToken());
        newBuilder.addQueryParameter("v", API_VERSION);
        newBuilder.addQueryParameter("ts", String.valueOf(LongPollServer.ts));

        if (LongPollServer.pts != -1) {
            newBuilder.addQueryParameter("pts", String.valueOf(LongPollServer.pts));
        }

        Request request = new Request.Builder().url(newBuilder.build()).build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.body() == null) {
                result.error = new Error("response body is null");
                result.errTextMsg = "err read response body";
                return result;
            }

            String body = response.body().string();

            //Log.d("ApiVK", " get new msgs body:" + body);

            JSONObject mainObject = new JSONObject(body);
            JSONArray messagesArray = mainObject.getJSONObject("response").getJSONObject("messages").getJSONArray("items");

            ArrayList<Message> newMsgs = new  ArrayList<Message>();

            for (int i = 0; i < messagesArray.length(); i++) {

                JSONObject oneMessageJSON = messagesArray.getJSONObject(i);

                Message message = new Message();

                message.text = oneMessageJSON.getString("text");

                // add only text msg
                if (message.text.equals("")) {
                    continue;
                }

                // skip deleting event
                if (oneMessageJSON.toString().contains("deleted")) {
                    continue;
                }

                message.fromId = Integer.parseInt(oneMessageJSON.getString("from_id"));
                message.date = Integer.parseInt(oneMessageJSON.getString("date"));
                message.messageId = Integer.parseInt(oneMessageJSON.getString("id"));

                if (message.fromId == VkSingleton.getUserId()){
                    message.myMsg = true;
                }

                newMsgs.add(message);
            }

            LongPollServer.pts = Integer.parseInt(mainObject.getJSONObject("response").getString("new_pts"));

            result.result = newMsgs;

            return result;

        }
        catch (IOException e){
            Log.e("ApiVK", "error in getting response get request okhttp");
            result.error = new Error("error in getting response get request okhttp");
            result.errTextMsg = "error in getting response get request okhttp";
        }
        catch (JSONException e){
            Log.e("ApiVK", "error parse Json:", e);
            result.error = new Error("error parse Json" + e);
            result.errTextMsg = "error parse Json";
        }
        catch (NumberFormatException e){
            Log.e("ApiVK", "error in Integer.parseInt", e);
            result.error = new Error("error in Integer.parseInt" + e);
            result.errTextMsg = "error in Integer.parseInt";
        }
        catch (Exception e){
            Log.e("ApiVK", "some err", e);
            result.error = new Error("some err");
            result.errTextMsg = "some err";
        }

        return result;
    }





    // result -> VkChatWithUsers{ArrayList<VkChat> chats; HashMap<Integer,VkUser> users}
    public static  CommandResult getChatsWithUsers(int start, int end) {

        CommandResult result = new CommandResult();

        if (VkSingleton.getAccessToken().equals("") || VkSingleton.getUserId() == -1) {
            result.error = new Error("no session");
            result.errTextMsg = "you must login before this request";
            return result;
        }

        final String method = "method/messages.getConversations";

        // todo not work =(
        final String fields = "id,profiles,about,status,online,last_name,first_name,online,photo_200"; //photo_200

        int count = end - start;

        if (count < 0 || count > MAX_REQUEST_ITEM_COUNT) {
            result.error = new Error("bad chat count, must be (0,200]");
            result.errTextMsg = "bad chat count, must be (0,200], count = count = end - start";
            return result;
        }

        HttpUrl.Builder httpBuider = Objects.requireNonNull(HttpUrl.parse(API_HOST + method))
                .newBuilder();

        httpBuider.addQueryParameter("access_token", VkSingleton.getAccessToken());
        httpBuider.addQueryParameter("v", API_VERSION);
        httpBuider.addQueryParameter("offset", String.valueOf(start));
        httpBuider.addQueryParameter("count", String.valueOf(count));
        httpBuider.addQueryParameter("extended", "1");
        httpBuider.addQueryParameter("fields", fields);

        Request request = new Request.Builder().url(httpBuider.build()).build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.body() == null) {
                result.error = new Error("response body is null");
                result.errTextMsg = "err read response body";
                return result;
            }
            String body = response.body().string();


//            Log.d("ApiVK get chat U:", "response.body:" + body);



            JSONObject mainObject = new JSONObject(body);
            JSONArray jsonConversations = mainObject.getJSONObject("response").getJSONArray("items");

            // get chats
            ArrayList<VkChat> chatList =  new ArrayList<VkChat>();

            for (int i = 0; i < jsonConversations.length(); i++) {

                VkChat vkChat = new VkChat();

                String type = jsonConversations.getJSONObject(i).getJSONObject("conversation").getJSONObject("peer").getString("type");
                vkChat.chatWithId = Integer.parseInt(jsonConversations.getJSONObject(i).getJSONObject("conversation").getJSONObject("peer").getString("id"));
//                Log.d("ApiVK get chats:", "type:" + type);


                JSONObject lastMessageObj = jsonConversations.getJSONObject(i).getJSONObject("last_message");
                vkChat.text = lastMessageObj.getString("text");
                vkChat.date = Integer.parseInt(lastMessageObj.getString("date"));
                vkChat.lastMsgFromId = Integer.parseInt(lastMessageObj.getString("from_id"));
                vkChat.lasMsgId = Integer.parseInt(lastMessageObj.getString("id"));

                if (type.equals("user")) {
                    vkChat.chatType = VkChat.CHAT_TYPE_USER;

                }
                if (type.equals("chat")) {
                    vkChat.chatType = VkChat.CHAT_TYPE_GROUP_CHAT;
                }
                // todo add here public group

                chatList.add(vkChat);
            }

            // get users
            HashMap<Integer, VkUser> chatUsers  = new HashMap<Integer, VkUser>();

            JSONArray jsonUsers = mainObject.getJSONObject("response").getJSONArray("profiles");

            for (int i = 0; i < jsonUsers.length(); i++) {


                VkUser vkUser = new VkUser();

                JSONObject oneUserJsonObj = jsonUsers.getJSONObject(i);

                vkUser.id = Integer.parseInt(oneUserJsonObj.getString("id"));

                // skip group chat
                if (vkUser.id > START_ID_GROUP_CHAT)
                    continue;

                // skip me
//                if (vkUser.id == VkSingleton.getUserId())
//                    continue;

                vkUser.firstName = oneUserJsonObj.getString("first_name");
                vkUser.lastName = oneUserJsonObj.getString("last_name");
                String is_closed = oneUserJsonObj.getString("is_closed");

                vkUser.isClosed = is_closed.equals("true");


//                if (!is_closed.equals("true"))
//                    vkUser.about = oneUserJsonObj.getString("about");

                vkUser.status = oneUserJsonObj.getString("status");
                String online = oneUserJsonObj.getString("online");
                vkUser.online = online.equals("1");
                vkUser.photo200Square = oneUserJsonObj.getString("photo_200");


                chatUsers.put(vkUser.id, vkUser);
            }


            VkChatWithUsers vkChatWithUsers = new VkChatWithUsers();
            vkChatWithUsers.chats = chatList;
            vkChatWithUsers.users = chatUsers;

            result.result = vkChatWithUsers;


//            for (HashMap.Entry<Integer,VkUser> entry : vkChatWithUsers.users.entrySet()) {
//                Integer key = entry.getKey();
//                VkUser user = entry.getValue();
//
//                String showLog = "key:" + key + "\n" + user.firstName + " "  + user.lastName + "\n"
//                        + "id:" + user.id + "\tphoto" + user.photo200Square;
//
//                Log.d("ApiVK", showLog);
//            }

            return result;

        }
        catch (IOException e) {
            Log.e("ApiVK", "error in getting response get request okhttp");
            result.error = new Error("error in getting response get request okhttp");
            result.errTextMsg = "error in getting response get request okhttp";
        }
        catch (JSONException e){
            Log.e("ApiVK", "error in JSONObject", e);
            result.error = new Error("error in JSONObject");
            result.errTextMsg = "error in JSONObject";
        }
        catch (NumberFormatException e){
            Log.e("ApiVK", "error in Integer.parseInt", e);
            result.error = new Error("error in Integer.parseInt");
            result.errTextMsg = "error in Integer.parseInt";
        }
        catch (Exception e){
            Log.e("ApiVK", "some err", e);
            result.error = new Error("some err");
            result.errTextMsg = "some err";
        }
        return result;
    }




}
