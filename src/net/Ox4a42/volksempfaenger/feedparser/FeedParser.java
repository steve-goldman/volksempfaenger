package net.Ox4a42.volksempfaenger.feedparser;

import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class FeedParser {
	private final String TAG = "FeedParser";

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
		Stack<String> parents = new Stack<String>();
		private final String ATOM_NS = "http://www.w3.org/2005/Atom";

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
			parents.push(fullName);
		}
		
		private void onText() {
			if(parents.peek() == ATOM_NS + ":title") {
				String copy = parents.pop();
				if(parents.peek() == ATOM_NS + ":feed") {
					// feed title
				}
				else if(parents.peek() == ATOM_NS + ":entry") {
					// entry title
				}
				parents.push(copy);
			}
		}
		
		private void onEndTag() {
			parents.pop();
		}
	}
}
