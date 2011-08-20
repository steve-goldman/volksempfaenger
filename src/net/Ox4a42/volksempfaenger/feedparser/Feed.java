package net.Ox4a42.volksempfaenger.feedparser;

import java.util.ArrayList;
import java.util.List;


public class Feed {
	public String getUrl() {
		return "http://example.com/feed.xml";
	}
	
	public String getTitle() {
		return "Example Podcast Feed";
	}
	
	public String getWebsite() {
		return "http://example.com/";
	}
	
	public List<FeedItem> getItems() {
		List<FeedItem> l = new ArrayList<FeedItem>();
		l.add(new FeedItem());
		l.add(new FeedItem());
		return l;
	}
}
