package com.inplace.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class ApiImageLoader {

    private static final int defaultWidth = 200;
    private static final int defaultHeight = 200;

    // todo using lazy singleton in ApiImageLoader class
    private static ImageLoader imageLoader = ImageLoader.getInstance();

    public static Bitmap getImageByUrl(String imageUri, Context context) {
        Log.d("Image Loader", "download image by url:" + imageUri);
        // todo fix many times init
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        return imageLoader.loadImageSync(imageUri, new ImageSize(defaultWidth, defaultHeight));
    }

}
