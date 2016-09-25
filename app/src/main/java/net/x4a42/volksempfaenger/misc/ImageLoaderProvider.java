package net.x4a42.volksempfaenger.misc;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ImageLoaderProvider
{
    private static ImageLoader instance;
    private final Context      context;

    public ImageLoaderProvider(Context context)
    {
        this.context = context;
    }

    public ImageLoader get()
    {
        if (instance == null)
        {
            instance = ImageLoader.getInstance();
            instance.init(getConfiguration());
        }

        return instance;
    }

    private ImageLoaderConfiguration getConfiguration()
    {
        return new ImageLoaderConfiguration.Builder(context)
                .memoryCacheSize(1024 * 1024)
                .diskCacheSize(12 * 1024 * 1024)
                .build();
    }
}
