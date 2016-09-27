package net.x4a42.volksempfaenger.service.feedsync;

class FeedSyncServiceProxyBuilder
{
    public FeedSyncServiceProxy build(FeedSyncService service)
    {
        IntentParser intentParser
                = new IntentParserBuilder().build(service);

        FeedSyncTaskBuilder taskBuilder
                = new FeedSyncTaskBuilder();

        FeedSyncServiceProxy proxy = new FeedSyncServiceProxy(service, intentParser, taskBuilder);

        intentParser.setListener(proxy);

        return proxy;
    }
}
