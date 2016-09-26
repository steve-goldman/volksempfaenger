package net.x4a42.volksempfaenger.ui.subscriptiongrid.view;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastPathProvider;
import net.x4a42.volksempfaenger.misc.ImageViewAwareBuilder;

public class GridViewHolder implements ImageLoadingListener
{
    private final View                  view;
    private final TextView              titleView;
    private final ImageView             logoView;
    private final ProgressBar           progressBar;
    private final PodcastPathProvider   podcastPathProvider;
    private final ImageLoader           imageLoader;
    private final ImageViewAwareBuilder imageViewAwareBuilder;
    private Podcast                     podcast;

    public GridViewHolder(View                  view,
                          TextView              titleView,
                          ImageView             logoView,
                          ProgressBar           progressBar,
                          PodcastPathProvider   podcastPathProvider,
                          ImageLoader           imageLoader,
                          ImageViewAwareBuilder imageViewAwareBuilder)
    {
        this.view                  = view;
        this.titleView             = titleView;
        this.logoView              = logoView;
        this.progressBar           = progressBar;
        this.podcastPathProvider   = podcastPathProvider;
        this.imageLoader           = imageLoader;
        this.imageViewAwareBuilder = imageViewAwareBuilder;
    }

    public View getView()
    {
        return view;
    }

    public Podcast getPodcast()
    {
        return podcast;
    }

    public void set(Podcast podcast)
    {
        this.podcast = podcast;
        setTitle();
        setLogo();
    }

    private void setTitle()
    {
        titleView.setText(podcast.getTitle());
    }

    private void setLogo()
    {
        String url = podcastPathProvider.getLogoUrl(podcast);
        logoView.setImageResource(android.R.color.transparent);
        progressBar.setVisibility(View.VISIBLE);
        imageLoader.displayImage(
                url, imageViewAwareBuilder.build(logoView), null, null, this, null);
    }

    @Override
    public void onLoadingStarted(String imageUri, View view)
    {
        // no-op
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason)
    {
        // no-op
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
    {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view)
    {
        // no-op
    }
}
