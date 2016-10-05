package net.x4a42.volksempfaenger.service.playlistdownload;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class EpisodeDownloadEventReceiver
{
    private final EventBus               eventBus;
    private EpisodeDownloadEventListener listener;

    public EpisodeDownloadEventReceiver(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }

    public EpisodeDownloadEventReceiver setListener(EpisodeDownloadEventListener listener)
    {
        this.listener = listener;
        return this;
    }

    public void subscribe()
    {
        eventBus.register(this);
    }

    public void unsubscribe()
    {
        eventBus.unregister(this);
    }

    @Subscribe
    public void onEvent(EpisodeDownloadEvent event)
    {
        listener.onEpisodeDownloadEvent(event);
    }
}
