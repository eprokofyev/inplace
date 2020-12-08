package com.inplace.api.vk;

import java.util.concurrent.ThreadLocalRandom;

public class VkSingleton {

    private VkSingleton() {}

    private static final int START_RANDOM_ID = 100000;
    private static final int RANDOM_ID_DELTA = 10000;

    private static int randomIdStart = ThreadLocalRandom.current().
            nextInt(START_RANDOM_ID + RANDOM_ID_DELTA, Integer.MAX_VALUE - START_RANDOM_ID);


    public static synchronized int getNextNumber() {
        return ++randomIdStart;
    }


    public static class LongPollServer {

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

}