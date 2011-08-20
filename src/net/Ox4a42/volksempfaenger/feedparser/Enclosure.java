package net.Ox4a42.volksempfaenger.feedparser;

public class Enclosure {
	private String title, url, mime;
	private Integer length;
	private FeedItem feedItem;

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public String getMime() {
		return mime;
	}
	
	public Integer getLength() {
		return length;
	}
	
	public FeedItem getFeedItem() {
		return feedItem;
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
	
	public void setLength(Integer length) {
		this.length = length;
	}
	
	public void setFeedItem(FeedItem feedItem) {
		this.feedItem = feedItem;
	}
}
