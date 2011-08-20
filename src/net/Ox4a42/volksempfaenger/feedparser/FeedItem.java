package net.Ox4a42.volksempfaenger.feedparser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedItem {
	private String title, url, description;
	private Feed feed;
	private Date date;
	private List<Enclosure> enclosures = new ArrayList<Enclosure>();
	
	public String getTitle() {
		return title;
	}
	
	public Feed getFeed() {
		return feed;
	}
	
	public Date getDate() {
		return date;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getDescription() {
		return description;
	}
	
	public List<Enclosure> getEnclosures() {
		return enclosures;
	}


	public void setTitle(String title) {
		this.title = title;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFeed(Feed feed) {
		this.feed = feed;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
