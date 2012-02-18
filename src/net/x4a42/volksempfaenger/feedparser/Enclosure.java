package net.x4a42.volksempfaenger.feedparser;

public class Enclosure {
	private String title, url, mime, file;
	private long size;
	private FeedItem feedItem;
	private int state;

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public String getMime() {
		return mime;
	}

	public long getSize() {
		return size;
	}

	public FeedItem getFeedItem() {
		return feedItem;
	}

	public int getState() {
		return state;
	}

	public String getFile() {
		return file;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setFeedItem(FeedItem feedItem) {
		this.feedItem = feedItem;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setFile(String file) {
		this.file = file;
	}
}
