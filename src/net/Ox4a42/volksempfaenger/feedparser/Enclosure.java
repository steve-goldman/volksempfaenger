package net.Ox4a42.volksempfaenger.feedparser;

public class Enclosure {
	public FeedItem getFeedItem() {
		return new FeedItem();
	}
	
	public String getTitle() {
		return "EP001.mp3";
	}
	
	public String getURL() {
		return "http://example.org/podcast/ep001.mp3";
	}
	
	public String getMIME() {
		return "audio/mpeg";
	}
	
	public Integer getLength() {
		return 10485760;
	}
}
