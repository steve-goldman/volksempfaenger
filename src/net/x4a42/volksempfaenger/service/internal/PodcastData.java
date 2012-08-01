package net.x4a42.volksempfaenger.service.internal;

import net.x4a42.volksempfaenger.net.CacheInformation;
import android.net.Uri;

public class PodcastData {
	
	public Uri uri;
	public long id;
	public String title;
	public String feed;
	public CacheInformation cacheInfo;
	public boolean firstSync;
	public boolean forceUpdate;
	
}
