package net.x4a42.volksempfaenger.misc;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class ImageViewAwareBuilder
{
    public ImageViewAware build(ImageView imageView)
    {
        return new ImageViewAware(imageView);
    }
}
