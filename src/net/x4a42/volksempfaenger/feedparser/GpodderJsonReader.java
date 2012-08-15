package net.x4a42.volksempfaenger.feedparser;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

import net.x4a42.volksempfaenger.feedparser.Enums.GpodderKey;
import android.util.JsonReader;

public class GpodderJsonReader {
	public final static String KEY_TITLE = "title";
	public final static String KEY_URL = "url";
	public final static String KEY_DESCRIPTION = "description";
	public final static String KEY_WEBSITE = "website";
	public final static String KEY_SCALED_LOGO = "scaled_logo_url";

	private final JsonReader reader;
	private final GpodderJsonReaderListener listener;

	public GpodderJsonReader(Reader in, GpodderJsonReaderListener listener) {
		reader = new JsonReader(in);
		this.listener = listener;
	}

	public void read() throws IOException {
		try {
			readPodcastArray();
		} finally {
			reader.close();
		}
	}

	public void readPodcastArray() throws IOException {
		reader.beginArray();
		while (reader.hasNext()) {
			readPodcast();
		}
		reader.endArray();
	}

	public void readPodcast() throws IOException {
		HashMap<String, String> podcast = new HashMap<String, String>();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			GpodderKey key = StringLookup.lookupGpodderKey(name);
			switch (key) {
			case DESCRIPTION:
				podcast.put(KEY_DESCRIPTION, reader.nextString());
				break;
			case SCALED_LOGO_URL:
				podcast.put(KEY_SCALED_LOGO, reader.nextString());
				break;
			case TITLE:
				podcast.put(KEY_TITLE, reader.nextString());
				break;
			case URL:
				podcast.put(KEY_URL, reader.nextString());
				break;
			case WEBSITE:
				podcast.put(KEY_WEBSITE, reader.nextString());
				break;
			case UNKNOWN:
				reader.skipValue();
				break;
			}
		}
		reader.endObject();
		listener.onPodcast(podcast);
	}
}
