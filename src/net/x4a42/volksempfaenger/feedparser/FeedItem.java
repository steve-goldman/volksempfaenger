package net.x4a42.volksempfaenger.feedparser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedItem implements Cloneable {
	public String title, url, description, itemId, flattrUrl;
	public Feed feed;
	public Date date;
	public List<Enclosure> enclosures = new ArrayList<Enclosure>();

	public void reset() {
		title = null;
		url = null;
		description = null;
		itemId = null;
		flattrUrl = null;
		feed = null;
		date = null;
		enclosures = null;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
