package net.x4a42.volksempfaenger.service.syncall;

import android.app.Service;

import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDaoWrapper;
import net.x4a42.volksempfaenger.service.feedsync.FeedSyncServiceIntentProvider;
import net.x4a42.volksempfaenger.service.feedsync.FeedSyncServiceIntentProviderBuilder;

class SyncAllServiceProxyBuilder
{
    public SyncAllServiceProxy build(Service service)
    {
        IntentParser      intentParser = new IntentParser();
        PodcastDaoWrapper podcastDao   = new PodcastDaoBuilder().build(service);

        FeedSyncServiceIntentProvider intentProvider
                = new FeedSyncServiceIntentProviderBuilder().build(service);

        SyncAllServiceProxy proxy = new SyncAllServiceProxy(service,
                                                            intentParser,
                                                            podcastDao,
                                                            intentProvider);

        intentParser.setListener(proxy);

        return proxy;
    }
}
