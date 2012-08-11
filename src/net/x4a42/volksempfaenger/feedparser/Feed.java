package net.x4a42.volksempfaenger.feedparser;

import java.util.ArrayList;
import java.util.List;

public class Feed {
	public long local_id;
	public String url, title, website, description, image;
	public List<FeedItem> items = new ArrayList<FeedItem>();
}
