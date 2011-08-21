package net.x4a42.volksempfaenger.feedparser;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class FeedParser {
	private final String TAG = getClass().getSimpleName();

	public static Feed parse(Reader reader) throws FeedParserException,
			IOException {
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();

			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();

			parser.setInput(reader);
			ParserHelper parserHelper;
			parserHelper = new ParserHelper(parser);
			return parserHelper.getFeed();
		} catch (XmlPullParserException e) {
			throw new FeedParserException();
		}
	}

	private static class ParserHelper {
		private static enum Namespace {
			NONE, ATOM, RSS, UNKNOWN
		}

		private static enum Tag {
			UNKNOWN, ATOM_FEED, ATOM_TITLE, ATOM_ENTRY, ATOM_LINK, ATOM_SUMMARY, ATOM_CONTENT, ATOM_PUBLISHED, ATOM_SUBTITLE, RSS_TOPLEVEL, RSS_CHANNEL, RSS_ITEM, RSS_TITLE, RSS_LINK, RSS_DESCRIPTION, RSS_ENCLOSURE, RSS_PUB_DATE
		}

		private static final String TAG = "ParserHelper";
		private XmlPullParser parser;
		private Feed feed = new Feed();
		private FeedItem feedItem = null;
		Stack<Tag> parents = new Stack<Tag>();
		private boolean currentItemHasSummary = false;
		private boolean isFeed = false;
		Namespace currentNS = Namespace.NONE;

		private static final String ATOM_ATTR_HREF = "href";
		private static final String ATOM_ATTR_REL = "rel";
		private static final String ATOM_ATTR_TYPE = "type";
		private static final String ATOM_ATTR_LENGTH = "length";
		private static final String ATOM_ATTR_TITLE = "title";
		private static final String ATOM_REL_ENCLOSURE = "enclosure";
		private static final String ATOM_REL_ALTERNATE = "alternate";
		private static final String ATOM_REL_SELF = "self";

		private static final String RSS_ATTR_URL = "url";
		private static final String RSS_ATTR_TYPE = "type";
		private static final String RSS_ATTR_LENGTH = "length";

		private static final String MIME_HTML = "text/html";
		private static final String MIME_XHTML = "text/xhtml";

		public ParserHelper(XmlPullParser parser)
				throws XmlPullParserException, IOException, NotAFeedException {
			this.parser = parser;
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
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
			if (!isFeed) {
				throw new NotAFeedException();
			}

		}

		public Feed getFeed() {
			return feed;
		}

		private void onStartTag() {
			String nsString = parser.getNamespace();
			currentNS = getNamespace(nsString);
			Tag tag = getTag(nsString + ":" + parser.getName());

			if (!isFeed) {
				// is current element one of the toplevel elements
				if (((currentNS == Namespace.ATOM) && tag == Tag.ATOM_FEED)
						|| ((currentNS == Namespace.NONE) && tag == Tag.RSS_TOPLEVEL)
						|| ((currentNS == Namespace.RSS) && tag == Tag.RSS_TOPLEVEL)) {

				}
				isFeed = true;
			}

			if (currentNS == Namespace.ATOM) {
				onStartTagAtom(tag);
			} else {
				onStartTagRss(tag);
			}
			parents.push(tag);
		}

		private void onStartTagAtom(Tag tag) {
			switch (tag) {
			case ATOM_ENTRY:
				feedItem = new FeedItem();
				feedItem.setFeed(feed);
				currentItemHasSummary = false;
				break;
			case ATOM_LINK:
				String rel = parser.getAttributeValue("", ATOM_ATTR_REL);
				if (rel == null) {
				} else if (rel.equals(ATOM_REL_ENCLOSURE)) {
					if (parents.peek() == Tag.ATOM_ENTRY) {
						Enclosure enclosure = new Enclosure();
						enclosure.setFeedItem(feedItem);
						enclosure.setUrl(parser.getAttributeValue("",
								ATOM_ATTR_HREF));
						enclosure.setMime(parser.getAttributeValue("",
								ATOM_ATTR_TYPE));
						enclosure.setTitle(parser.getAttributeValue("",
								ATOM_ATTR_TITLE));

						String length = parser.getAttributeValue("",
								ATOM_ATTR_LENGTH);
						if (length != null) {
							enclosure.setSize(Long.parseLong(length));
						}
						feedItem.getEnclosures().add(enclosure);
					}
				} else if (rel.equals(ATOM_REL_ALTERNATE)) {
					String type = parser.getAttributeValue("", ATOM_ATTR_TYPE);
					if (parents.peek() == Tag.ATOM_ENTRY) {
						if (type == null || type.equals(MIME_HTML)
								|| type.equals(MIME_XHTML)) {
							// actually there can be multiple "alternate links"
							// this uses the LAST alternate link as the URL for
							// the FeedItem
							feedItem.setUrl(parser.getAttributeValue("",
									ATOM_ATTR_HREF));
						}
					} else if (parents.peek() == Tag.ATOM_FEED) {
						if (type == null || type.equals(MIME_HTML)
								|| type.equals(MIME_XHTML)) {
							// same issue as above with multiple alternate links
							feed.setWebsite(parser.getAttributeValue("",
									ATOM_ATTR_HREF));
						}
					}
				} else if (rel.equals(ATOM_REL_SELF)) {
					if (parents.peek() == Tag.ATOM_FEED) {
						feed.setUrl(parser
								.getAttributeValue("", ATOM_ATTR_HREF));
					}
				}
				break;
			}

		}

		private void onStartTagRss(Tag tag) {
			switch (tag) {
			case RSS_ITEM:
				feedItem = new FeedItem();
				feedItem.setFeed(feed);
				break;
			case RSS_ENCLOSURE:
				if (parents.peek() == Tag.RSS_ITEM) {
					Enclosure enclosure = new Enclosure();
					enclosure.setFeedItem(feedItem);
					enclosure
							.setUrl(parser.getAttributeValue("", RSS_ATTR_URL));
					enclosure.setMime(parser.getAttributeValue("",
							RSS_ATTR_TYPE));

					String length = parser.getAttributeValue("",
							RSS_ATTR_LENGTH);
					if (length != null) {
						enclosure.setSize(Long.parseLong(length));
					}
					feedItem.getEnclosures().add(enclosure);
				}
				break;
			}
		}

		private void onText() {
			if (currentNS == Namespace.ATOM) {
				onTextAtom();
			} else if (currentNS == Namespace.NONE
					|| currentNS == Namespace.RSS) {
				onTextRss();
			}
		}

		private void onTextAtom() {
			switch (parents.peek()) {
			case ATOM_TITLE:
				Tag copy = parents.pop();
				if (parents.peek() == Tag.ATOM_FEED) {
					// feed title
					feed.setTitle(parser.getText());
				} else if (parents.peek() == Tag.ATOM_ENTRY) {
					// entry title
					feedItem.setTitle(parser.getText());
				}
				parents.push(copy);
				break;
			case ATOM_SUMMARY:
				Tag copy1 = parents.pop();
				if (parents.peek() == Tag.ATOM_ENTRY) {
					currentItemHasSummary = true;
					feedItem.setDescription(parser.getText());
				}
				parents.push(copy1);
				break;
			case ATOM_CONTENT:
				if (!currentItemHasSummary
						&& (parents.peek() == Tag.ATOM_ENTRY)) {
					feedItem.setDescription(parser.getText());
				}
				break;
			case ATOM_PUBLISHED:
				Tag copy2 = parents.pop();
				if (parents.peek() == Tag.ATOM_ENTRY) {
					try {
						feedItem.setDate(parseAtomDate(parser.getText()));
					} catch (IndexOutOfBoundsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				parents.push(copy2);
				break;
			case ATOM_SUBTITLE:
				feed.setDescription(parser.getText());
				break;
			}
		}

		private void onTextRss() {
			switch (parents.peek()) {
			case RSS_TITLE:
				Tag copy1 = parents.pop();
				switch (parents.peek()) {
				case RSS_CHANNEL:
					feed.setTitle(parser.getText());
					break;

				case RSS_ITEM:
					feedItem.setTitle(parser.getText());
					break;
				}
				parents.push(copy1);
				break;
			case RSS_PUB_DATE:
				Tag copy2 = parents.pop();
				if (parents.peek() == Tag.RSS_ITEM) {
					feedItem.setDate(parseRssDate(parser.getText()));
				}
				parents.push(copy2);
				break;
			case RSS_LINK:
				Tag copy3 = parents.pop();
				switch (parents.peek()) {
				case RSS_ITEM:
					feedItem.setUrl(parser.getText());
					break;
				case RSS_CHANNEL:
					feed.setWebsite(parser.getText());
					break;
				}
				parents.push(copy3);
				break;
			case RSS_DESCRIPTION:
				Tag copy4 = parents.pop();
				switch (parents.peek()) {
				case RSS_ITEM:
					feedItem.setDescription(parser.getText());
					break;
				case RSS_CHANNEL:
					feed.setDescription(parser.getText());
					break;
				}
				parents.push(copy4);
				break;
			}
		}

		private void onEndTag() {
			currentNS = getNamespace(parser.getNamespace());
			Tag tag = parents.pop();
			if (currentNS == Namespace.ATOM) {
				onEndTagAtom(tag);
			} else if (currentNS == Namespace.NONE
					|| currentNS == Namespace.RSS) {
				onEndTagRss(tag);
			}
		}

		private void onEndTagAtom(Tag tag) {
			if (tag == Tag.ATOM_ENTRY) {
				feed.getItems().add(feedItem);
				feedItem = null;
			}
		}

		private void onEndTagRss(Tag tag) {
			if (tag == Tag.RSS_ITEM) {
				feed.getItems().add(feedItem);
				feedItem = null;
			}

		}

		private Date parseAtomDate(String datestring)
				throws java.text.ParseException, IndexOutOfBoundsException {
			// original version by Chad Okere (ceothrow1 at gmail dotcom)
			// http://cokere.com/RFC3339Date.txt
			// dirty version - write a new one TODO

			Date d = new Date();

			// if there is no time zone, we don't need to do any special
			// parsing.
			if (datestring.endsWith("Z")) {
				try {
					SimpleDateFormat s = new SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ss'Z'");// spec for RFC3339
					d = s.parse(datestring);
				} catch (java.text.ParseException pe) {// try again with
														// optional decimals
					SimpleDateFormat s = new SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");// spec for
																// RFC3339 (with
																// fractional
																// seconds)
					s.setLenient(true);
					d = s.parse(datestring);
				}
				return d;
			}

			// step one, split off the timezone.
			String firstpart = datestring.substring(0,
					datestring.lastIndexOf('-'));
			String secondpart = datestring.substring(datestring
					.lastIndexOf('-'));

			// step two, remove the colon from the timezone offset
			secondpart = secondpart.substring(0, secondpart.indexOf(':'))
					+ secondpart.substring(secondpart.indexOf(':') + 1);
			datestring = firstpart + secondpart;
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");// spec
																				// for
																				// RFC3339
			try {
				d = s.parse(datestring);
			} catch (java.text.ParseException pe) { // try again with optional
													// decimals
				s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ"); // spec
																			// for
																			// RFC3339
																			// (with
																			// fractional
																			// seconds)
				s.setLenient(true);
				d = s.parse(datestring);
			}
			return d;
		}

		private static final SimpleDateFormat formats[] = new SimpleDateFormat[] {
				new SimpleDateFormat("EEE, d MMM yy HH:mm:ss z", Locale.US),
				new SimpleDateFormat("EEE, d MMM yy HH:mm z", Locale.US),
				new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US),
				new SimpleDateFormat("EEE, d MMM yyyy HH:mm z", Locale.US),
				new SimpleDateFormat("d MMM yy HH:mm z", Locale.US),
				new SimpleDateFormat("d MMM yy HH:mm:ss z", Locale.US),
				new SimpleDateFormat("d MMM yyyy HH:mm z", Locale.US),
				new SimpleDateFormat("d MMM yyyy HH:mm:ss z", Locale.US), };

		private Date parseRssDate(String datestring) {
			// dirty version - write a new one TODO
			Date date = null;
			SimpleDateFormat format;
			if (datestring.charAt(4) == ' ') {
				if (datestring.charAt(13) == ' ') {
					if (datestring.charAt(19) != ' ') {
						format = formats[0];
					} else {
						format = formats[1];
					}
				} else {
					if (datestring.charAt(21) != ' ') {
						format = formats[2];
					} else {
						format = formats[3];
					}
				}
			} else {
				if (datestring.charAt(8) == ' ') {
					if (datestring.charAt(14) == ' ') {
						format = formats[4];
					} else {
						format = formats[5];
					}
				} else {
					if (datestring.charAt(16) == ' ') {
						format = formats[6];
					} else {
						format = formats[7];
					}
				}
			}
			try {
				date = format.parse(datestring);
			} catch (ParseException e) {
				date = null;
			}
			// date can be null here
			return date;
		}

		static final String ATOM_NS = "http://www.w3.org/2005/Atom";
		static final String RSS_NS = "http://backend.userland.com/RSS2";

		private static Namespace getNamespace(String nsString) {
			if (nsString.equals("")) {
				return Namespace.NONE;
			} else if (nsString.equals(ATOM_NS)) {
				return Namespace.ATOM;
			} else if (nsString.equals(RSS_NS)) {
				return Namespace.RSS;
			} else {
				return Namespace.UNKNOWN;
			}
		}

		static final Map<String, Tag> lookupTable;
		static {
			Map<String, Tag> temp = new HashMap<String, Tag>();
			temp.put(ATOM_NS + ":feed", Tag.ATOM_FEED);
			temp.put(ATOM_NS + ":title", Tag.ATOM_TITLE);
			temp.put(ATOM_NS + ":entry", Tag.ATOM_ENTRY);
			temp.put(ATOM_NS + ":link", Tag.ATOM_LINK);
			temp.put(ATOM_NS + ":summary", Tag.ATOM_SUMMARY);
			temp.put(ATOM_NS + ":content", Tag.ATOM_CONTENT);
			temp.put(ATOM_NS + ":published", Tag.ATOM_PUBLISHED);
			temp.put(ATOM_NS + ":subtitle", Tag.ATOM_SUBTITLE);

			temp.put(RSS_NS + ":rss", Tag.RSS_TOPLEVEL);
			temp.put(RSS_NS + ":channel", Tag.RSS_CHANNEL);
			temp.put(RSS_NS + ":item", Tag.RSS_ITEM);
			temp.put(RSS_NS + ":title", Tag.RSS_TITLE);
			temp.put(RSS_NS + ":link", Tag.RSS_LINK);
			temp.put(RSS_NS + ":description", Tag.RSS_DESCRIPTION);
			temp.put(RSS_NS + ":enclosure", Tag.RSS_ENCLOSURE);
			temp.put(RSS_NS + ":pubDate", Tag.RSS_PUB_DATE);

			temp.put(":rss", Tag.RSS_TOPLEVEL);
			temp.put(":channel", Tag.RSS_CHANNEL);
			temp.put(":item", Tag.RSS_ITEM);
			temp.put(":title", Tag.RSS_TITLE);
			temp.put(":link", Tag.RSS_LINK);
			temp.put(":description", Tag.RSS_DESCRIPTION);
			temp.put(":enclosure", Tag.RSS_ENCLOSURE);
			temp.put(":pubDate", Tag.RSS_PUB_DATE);
			lookupTable = Collections.unmodifiableMap(temp);
		};

		private static Tag getTag(String fullName) {
			Tag tag = lookupTable.get(fullName);
			if (tag == null) {
				tag = Tag.UNKNOWN;
			}
			return tag;
		}
	}
}
