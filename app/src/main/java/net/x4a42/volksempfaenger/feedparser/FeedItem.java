package net.x4a42.volksempfaenger.feedparser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedItem {
	private static final String HMSPattern = "^(\\d+):(\\d+):(\\d+)$";
	private static final String MSPattern  = "^(\\d+):(\\d+)$";
	public String title, url, description, itemId, flattrUrl;
	public Feed feed;
	public Date date;
	public List<Enclosure> enclosures = new ArrayList<Enclosure>();
	public long duration;

	// TODO: this method is hacky
	public String getUrl()
	{
		if (url == null)
		{
			return enclosures.get(0).url;
		}
		return url;
	}

	public void setDuration(String durationStr)
	{
		try
		{
			if (durationStr.matches(HMSPattern))
			{
				String[] tokens = durationStr.split(":");
				duration = 1000 * (Integer.parseInt(tokens[2]) + 60 * (Integer.parseInt(tokens[1]) + 60 * Integer
						.parseInt(tokens[0])));
			}
			else if (durationStr.matches(MSPattern))
			{
				String[] tokens = durationStr.split(":");
				duration = 1000 * (Integer.parseInt(tokens[1]) + 60 * Integer.parseInt(tokens[0]));
			}
			else
			{
				duration = 1000 * Integer.parseInt(durationStr);
			}
		}
		catch (Exception e)
		{
			// pass-thru
		}
	}
}
