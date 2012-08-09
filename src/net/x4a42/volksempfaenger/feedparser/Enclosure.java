package net.x4a42.volksempfaenger.feedparser;

public class Enclosure implements Cloneable {
	public String title, url, mime, file;
	public long size;
	public FeedItem feedItem;
	public int state;

	public void reset() {
		title = null;
		url = null;
		mime = null;
		file = null;
		size = 0;
		feedItem = null;
		state = 0;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
