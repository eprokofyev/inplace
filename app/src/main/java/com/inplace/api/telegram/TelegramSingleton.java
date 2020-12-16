package com.inplace.api.telegram;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TelegramSingleton {

    public static Client client = null;

    public static TdApi.AuthorizationState authorizationState = null;
    public static volatile boolean haveAuthorization = false;

    public static final Lock authorizationLock = new ReentrantLock();
    public static final Condition gotAuthorization = authorizationLock.newCondition();


    public static final ConcurrentMap<Integer, TdApi.User> users = new ConcurrentHashMap<Integer, TdApi.User>();

    public static final ConcurrentMap<Integer, TdApi.UserFullInfo> usersFullInfo = new ConcurrentHashMap<Integer, TdApi.UserFullInfo>();

    public static volatile boolean needQuit = false;
    public static volatile boolean canQuit = false;


}
