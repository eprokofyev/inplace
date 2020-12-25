package com.inplace.services;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorServices {

    private static ExecutorService executorAPI = Executors.newCachedThreadPool();
    private static ExecutorService executorDB = new ThreadPoolExecutor(3, 7, 1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<Runnable>(15),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    Log.d("Tag", "Task was %d rejected due to capacity 1");
                }
            });// или просто Executors.newFixedThreadPool(THREAD_COUNT);
    // [1 2 3]
    // [4, 5] - LinkedBlockingQueue
    // [6,7,8] - reject !

    public static synchronized ExecutorService getInstanceAPI() {
        return executorAPI;
    }

    public static synchronized ExecutorService getInstanceDB() {

        return executorDB;
    }


}