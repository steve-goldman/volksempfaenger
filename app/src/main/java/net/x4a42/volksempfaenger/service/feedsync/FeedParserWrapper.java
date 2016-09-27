package net.x4a42.volksempfaenger.service.feedsync;

import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedParser;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;

import java.io.IOException;
import java.io.Reader;

class FeedParserWrapper
{
    private final Reader reader;

    public FeedParserWrapper(Reader reader)
    {
        this.reader = reader;
    }

    public Feed parse() throws IOException, FeedParserException
    {
        return FeedParser.parse(reader);
    }
}
