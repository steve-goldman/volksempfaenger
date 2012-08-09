package net.x4a42.volksempfaenger.service.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.feedparser.Enclosure;
import net.x4a42.volksempfaenger.feedparser.Feed;
import net.x4a42.volksempfaenger.feedparser.FeedItem;
import net.x4a42.volksempfaenger.feedparser.FeedParser;
import net.x4a42.volksempfaenger.feedparser.FeedParserException;
import net.x4a42.volksempfaenger.feedparser.FeedParserListener;

public class FeedParserRunnable extends UpdateRunnable {

	private PodcastData podcast;
	private File feedFile;

	public FeedParserRunnable(UpdateState update, PodcastData podcast,
			File feedFile) {
		super(update);
		this.podcast = podcast;
		this.feedFile = feedFile;
	}

	@Override
	public void run() {
		Log.v(this,
				"Started parsing " + podcast.toString() + " @ "
						+ System.currentTimeMillis());
		try {
			Reader reader = new BufferedReader(new FileReader(feedFile));
			FeedParser.parseEvented(reader, feedParserListener);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeedParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			feedFile.delete();
			Log.v(this, "Finished parsing " + podcast.toString() + " @ "
					+ System.currentTimeMillis());
		}
	}

	private final FeedParserListener feedParserListener = new FeedParserListener() {

		@Override
		public void onFeed(Feed feed) {
			// TODO Auto-generated method stub
			Log.d(this, feed.toString());
		}

		@Override
		public void onFeedItem(FeedItem feedItem) {
			// TODO Auto-generated method stub
			Log.d(this, feedItem.toString());
		}

		@Override
		public void onEnclosure(Enclosure enclosure) {
			// TODO Auto-generated method stub
			Log.d(this, enclosure.toString());
		}

	};

}
