package net.x4a42.volksempfaenger.feedparser;

import java.util.ArrayList;
import java.util.List;

public class Feed implements Cloneable {
	public String url, title, website, description, image;
	public List<FeedItem> items = new ArrayList<FeedItem>();

	public void reset() {
		url = null;
		title = null;
		website = null;
		description = null;
		image = null;
		items = null;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
