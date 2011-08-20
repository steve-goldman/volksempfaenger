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
	private final String ATOM_NS = "http://www.w3.org/2005/Atom";

	public static Feed parse(Reader reader) throws XmlPullParserException, IOException { 
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		
		parser.setInput(reader);
		
		ParserHelper parserHelper = new ParserHelper(parser);
		return parserHelper.getFeed();
	}
	
	private static class ParserHelper {
		private XmlPullParser parser;
		private Feed feed = new Feed();
		Stack<String> parents = new Stack<String>();

		public ParserHelper(XmlPullParser parser) throws XmlPullParserException, IOException {
			this.parser = parser;
	    	int eventType = parser.getEventType();
	        while(eventType != XmlPullParser.END_DOCUMENT) {
	        	switch(eventType) {
	        		case XmlPullParser.START_TAG:
	        			parents.push(parser.getNamespace() + ":" + parser.getName());
	        			break;
	        		case XmlPullParser.END_TAG:
	        			parents.pop();
	        			break;
	        	}
	        	eventType = parser.next();
	        }
		}
		
		public Feed getFeed() {
			return new Feed();
		}
	}
}
