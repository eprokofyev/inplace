package com.inplace.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServices {

    private static ExecutorService executorAPI = Executors.newCachedThreadPool();
    private static ExecutorService executorDB = Executors.newCachedThreadPool();

    public static synchronized ExecutorService getInstanceAPI() {
        return executorAPI;
    }

    public static synchronized ExecutorService getInstanceDB() {
        return executorDB;
    }

}