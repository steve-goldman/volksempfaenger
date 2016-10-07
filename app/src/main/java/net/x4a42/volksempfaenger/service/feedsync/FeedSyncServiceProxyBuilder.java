package net.x4a42.volksempfaenger.service.feedsync;

class FeedSyncServiceProxyBuilder
{
    public FeedSyncServiceProxy build(FeedSyncService service)
    {
        IntentParser intentParser
                = new IntentParserBuilder().build(service);

        FeedSyncTaskProxyBuilder taskProxyBuilder
                = new FeedSyncTaskProxyBuilder(service);

        FeedSyncTaskProvider taskProvider
                = new FeedSyncTaskProvider(service, taskProxyBuilder);

        FeedSyncServiceProxy proxy = new FeedSyncServiceProxy(intentParser, taskProvider);

        intentParser.setListener(proxy);

        return proxy;
    }
}
