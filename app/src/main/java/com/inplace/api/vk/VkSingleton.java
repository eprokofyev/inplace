package com.inplace.api.vk;

public class VkSingleton {

    private static String accessToken= "";
    private static int userId = -1;

    private VkSingleton() {}

    private static final VkSingleton instance = new VkSingleton();

    // todo make lazy
    public static VkSingleton getInstance() {
        return instance;
    }


    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        VkSingleton.accessToken = accessToken;
    }


    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        VkSingleton.userId = userId;
    }
}