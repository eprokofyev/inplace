package com.inplace.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.inplace.R;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;



public class ApiImageLoader {

    private static final int defaultWidth = 250;
    private static final int defaultHeight = 250;

    private static ImageLoader imageLoader = null;

    private static ApiImageLoader instance = null;

    public static synchronized ApiImageLoader getInstance(Context context) {
        if (instance == null) {
            instance = new ApiImageLoader();
            imageLoader = ImageLoader.getInstance();

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.error_load_image)
                    .showImageForEmptyUri(R.drawable.error_load_image)
                    .showImageOnFail(R.drawable.error_load_image)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .defaultDisplayImageOptions(options)
                    .memoryCache(new LruMemoryCache(5 * 1024 * 1024)) // 5 mb
                    .diskCacheSize(100 * 1024 * 1024) // 100 mb
                    .diskCacheFileCount(100)
                    .denyCacheImageMultipleSizesInMemory()
                    .writeDebugLogs()
                    .build();
            //ImageLoaderConfiguration.createDefault(context)
            imageLoader.init(config);
        }
        return instance;
    }


    public Bitmap getImageByUrl(String imageUri) {
        Log.d("Image Loader", "download image by url:" + imageUri);
        return imageLoader.loadImageSync(imageUri, new ImageSize(defaultWidth, defaultHeight));
    }

}

