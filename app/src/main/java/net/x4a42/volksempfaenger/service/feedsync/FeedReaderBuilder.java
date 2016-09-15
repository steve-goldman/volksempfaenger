package net.x4a42.volksempfaenger.service.feedsync;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;

class FeedReaderBuilder
{
    public Reader build(HttpURLConnection connection) throws IOException
    {
        return new InputStreamReader(connection.getInputStream());
    }
}
