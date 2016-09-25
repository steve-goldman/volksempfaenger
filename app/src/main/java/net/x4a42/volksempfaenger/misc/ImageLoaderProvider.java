package net.x4a42.volksempfaenger.misc;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import net.x4a42.volksempfaenger.R;

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
        int memoryCacheSize = context.getResources().getInteger(R.integer.image_loader_memory_cache_size);
        int diskCacheSize   = context.getResources().getInteger(R.integer.image_loader_disk_cache_size);
        int maxPixels       = context.getResources().getDimensionPixelSize(R.dimen.grid_column_width);

        return new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(getDisplayImageOptions())
                .memoryCacheSize(memoryCacheSize)
                .diskCacheSize(diskCacheSize)
                .memoryCacheExtraOptions(maxPixels, maxPixels)
                .build();
    }

    private DisplayImageOptions getDisplayImageOptions()
    {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }
}
