package net.Ox4a42.volksempfaenger.feedparser;

import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class FeedParser {
	private final String TAG = getClass().getSimpleName();

	public static Feed parse(Reader reader) throws XmlPullParserException, IOException { 
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();

		parser.setInput(reader);
		
		ParserHelper parserHelper = new ParserHelper(parser);
		return parserHelper.getFeed();
	}
	
	private static class ParserHelper {
		private static String TAG = "ParserHelper";
		private XmlPullParser parser;
		private Feed feed = new Feed();
		private FeedItem feedItem;
		Stack<String> parents = new Stack<String>();

		private final String ATOM_NS = "http://www.w3.org/2005/Atom";

		private final String ATOM_FEED = ATOM_NS + ":feed";
		private final String ATOM_TITLE = ATOM_NS + ":title";
		private final String ATOM_ENTRY = ATOM_NS + ":entry";
		private final String ATOM_LINK = ATOM_NS + ":link";
		private final String ATOM_REL_ENCLOSURE = "enclosure";

		public ParserHelper(XmlPullParser parser) throws XmlPullParserException, IOException {
			this.parser = parser;
	    	int eventType = parser.getEventType();
	        while(eventType != XmlPullParser.END_DOCUMENT) {
	        	switch(eventType) {
	        		case XmlPullParser.START_TAG:
	        			onStartTag();
	        			break;
	        		case XmlPullParser.TEXT:
	        			onText();
	        			break;
	        		case XmlPullParser.END_TAG:
	        			onEndTag();
	        			break;
	        	}
	        	eventType = parser.next();
	        }
		}
		
		public Feed getFeed() {
			return feed;
		}
		
		private void onStartTag() {
			String fullName = parser.getNamespace() + ":" + parser.getName();
			if(fullName.equals(ATOM_ENTRY)) {
				feedItem = new FeedItem();
			}
			else if(fullName.equals(ATOM_LINK)) {
				String rel = parser.getAttributeValue("", "rel");
				if(rel == null) {
					Log.d(TAG, "foo");
				}
				else if(rel.equals(ATOM_REL_ENCLOSURE)) {
					Log.d(TAG, "bar");
					Enclosure enclosure = new Enclosure();
					enclosure.setUrl(parser.getAttributeValue("", "href"));
					enclosure.setMime(parser.getAttributeValue("", "type"));
					enclosure.setTitle(parser.getAttributeValue("", "title"));

					String length = parser.getAttributeValue("", "length");
					if(length != null) {
						enclosure.setSize(Long.parseLong(length));
					}
					feedItem.getEnclosures().add(enclosure);
				}
			}
			
			parents.push(fullName);
		}
		
		private void onText() {
			if(parents.peek().equals(ATOM_TITLE)) {
				String copy = parents.pop();
				if(parents.peek().equals(ATOM_FEED)) {
					// feed title
					feed.setTitle(parser.getText());
				}
				else if(parents.peek().equals(ATOM_ENTRY)) {
					// entry title
					feedItem.setTitle(parser.getText());
				}
				parents.push(copy);
			}
		}
		
		private void onEndTag() {
			String fullName = parents.pop();
			if(fullName.equals(ATOM_ENTRY)) {
				feed.getItems().add(feedItem);
				feedItem = null;
			}
		}
	}
}
