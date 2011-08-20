package net.Ox4a42.volksempfaenger.feedparser;

import java.util.ArrayList;
import java.util.List;


public class Feed {
	private String url, title, website, description;
	private List<FeedItem> items = new ArrayList<FeedItem>();
	
	public String getUrl() {
		return url;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getWebsite() {
		return website;
	}
	
	public String getDescription() {
		return description;
	}
	
	public List<FeedItem> getItems() {
		return items;
	}


	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setWebsite(String website) {
		this.website = website;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
