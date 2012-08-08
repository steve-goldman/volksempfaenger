package net.x4a42.volksempfaenger.feedparser;

public interface FeedParserListener {
	/**
	 * Gets called at the end of the feed, when all metadata is available.
	 * 
	 * @param feed
	 *            Data of the feed. {@see Feed#items} may be null or empty.
	 */
	public void onFeed(Feed feed);

	/**
	 * Gets called at the end of a new feed item.
	 * 
	 * @param feedItem
	 *            Data of the feedItem. As this object is re-used internally, do
	 *            not modify or rely on it, after this method exits. {@see
	 *            FeedItem.enclosures} may be null or empty. {@see
	 *            FeedItem.feed} may be null.
	 */
	public void onFeedItem(FeedItem feedItem);

	/**
	 * Gets called on a new enclosure and belongs to the next feedItem, which
	 * means that onFeedItem is called after this.
	 * 
	 * @param enclosure
	 *            Data of the enclosure. As this object is re-used internally,
	 *            do not modify or rely on it, after this method exits. {@see
	 *            feedItem} may be null.
	 */
	public void onEnclosure(Enclosure enclosure);
}
