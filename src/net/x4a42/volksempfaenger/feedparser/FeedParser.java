package net.x4a42.volksempfaenger.feedparser;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class FeedParser {
	private final String TAG = getClass().getSimpleName();

	public static Feed parse(Reader reader) throws XmlPullParserException,
			IOException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();

		parser.setInput(reader);

		ParserHelper parserHelper = new ParserHelper(parser);
		return parserHelper.getFeed();
	}

	private static class ParserHelper {
		private static final String TAG = "ParserHelper";
		private XmlPullParser parser;
		private Feed feed = new Feed();
		private FeedItem feedItem;
		Stack<String> parents = new Stack<String>();
		private boolean currentItemHasSummary;

		private static final String ATOM_NS = "http://www.w3.org/2005/Atom";
		private static final String ATOM_FEED = ATOM_NS + ":feed";
		private static final String ATOM_TITLE = ATOM_NS + ":title";
		private static final String ATOM_ENTRY = ATOM_NS + ":entry";
		private static final String ATOM_LINK = ATOM_NS + ":link";
		private static final String ATOM_SUMMARY = ATOM_NS + ":summary";
		private static final String ATOM_CONTENT = ATOM_NS + ":content";
		private static final String ATOM_PUBLISHED = ATOM_NS + ":published";
		private static final String ATOM_SUBTITLE = ATOM_NS + ":subtitle";
		private static final String ATOM_ATTR_HREF = "href";
		private static final String ATOM_ATTR_REL = "rel";
		private static final String ATOM_ATTR_TYPE = "type";
		private static final String ATOM_ATTR_LENGTH = "length";
		private static final String ATOM_REL_ENCLOSURE = "enclosure";
		private static final String ATOM_REL_ALTERNATE = "alternate";
		private static final String ATOM_REL_SELF = "self";
		
		private static final String RSS_TOPLEVEL = "rss";
		private static final String RSS_CHANNEL = "channel";
		private static final String RSS_ITEM = "item";
		private static final String RSS_TITLE = "title";
		private static final String RSS_LINK = "link";
		private static final String RSS_DESCRIPTION = "description";
		private static final String RSS_ENCLOSURE = "enclosure";
		private static final String RSS_PUB_DATE = "pubDate";
		
		private static final String RSS2_NS = "http://backend.userland.com/RSS2";
		private static final String RSS_TOPLEVEL_NS = RSS2_NS + ":" + RSS_TOPLEVEL;
		private static final String RSS_CHANNEL_NS = RSS2_NS + ":" + RSS_CHANNEL;
		private static final String RSS_ITEM_NS = RSS2_NS + ":" + RSS_ITEM;
		private static final String RSS_TITLE_NS = RSS2_NS + ":" + RSS_TITLE;
		private static final String RSS_LINK_NS = RSS2_NS +  ":" + RSS_LINK;
		private static final String RSS_DESCRIPTION_NS = RSS2_NS + ":" + RSS_DESCRIPTION;
		private static final String RSS_ENCLOSURE_NS = RSS2_NS + ":" + RSS_ENCLOSURE;
		private static final String RSS_PUB_DATE_NS = RSS2_NS + ":" + RSS_PUB_DATE;
		
		private static final String MIME_HTML = "text/html";
		private static final String MIME_XHTML = "text/xhtml";

		public ParserHelper(XmlPullParser parser)
				throws XmlPullParserException, IOException {
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
		}

		public Feed getFeed() {
			return feed;
		}

		private void onStartTag() {
			String fullName = parser.getNamespace() + ":" + parser.getName();
			if (fullName.equals(ATOM_ENTRY)) {
				feedItem = new FeedItem();
				feedItem.setFeed(feed);
				currentItemHasSummary = false;
			} else if (fullName.equals(ATOM_LINK)) {
				String rel = parser.getAttributeValue("", ATOM_ATTR_REL);
				if (rel == null) {
				} else if (rel.equals(ATOM_REL_ENCLOSURE)) {
					if (parents.peek().equals(ATOM_ENTRY)) {
						Enclosure enclosure = new Enclosure();
						enclosure.setFeedItem(feedItem);
						enclosure.setUrl(parser.getAttributeValue("",
								ATOM_ATTR_HREF));
						enclosure.setMime(parser.getAttributeValue("",
								ATOM_ATTR_TYPE));
						enclosure.setTitle(parser
								.getAttributeValue("", "title"));

						String length = parser.getAttributeValue("",
								ATOM_ATTR_LENGTH);
						if (length != null) {
							enclosure.setSize(Long.parseLong(length));
						}
						feedItem.getEnclosures().add(enclosure);
					}
				} else if (rel.equals(ATOM_REL_ALTERNATE)) {
					String type = parser.getAttributeValue("", ATOM_ATTR_TYPE);
					if (parents.peek().equals(ATOM_ENTRY)) {
						if (type == null || type.equals(MIME_HTML)
								|| type.equals(MIME_XHTML)) {
							// actually there can be multiple "alternate links"
							// this uses the LAST alternate link as the URL for
							// the FeedItem
							feedItem.setUrl(parser.getAttributeValue("",
									ATOM_ATTR_HREF));
						}
					} else if (parents.peek().equals(ATOM_FEED)) {
						if (type == null || type.equals(MIME_HTML)
								|| type.equals(MIME_XHTML)) {
							// same issue as above with multiple alternate links
							feed.setWebsite(parser.getAttributeValue("",
									ATOM_ATTR_HREF));
						}
					}
				} else if (rel.equals(ATOM_REL_SELF)) {
					if (parents.peek().equals(ATOM_FEED)) {
						feed.setUrl(parser
								.getAttributeValue("", ATOM_ATTR_HREF));
					}
				}
			}

			parents.push(fullName);
		}

		private void onText() {
			if (parents.peek().equals(ATOM_TITLE)) {
				String copy = parents.pop();
				if (parents.peek().equals(ATOM_FEED)) {
					// feed title
					feed.setTitle(parser.getText());
				} else if (parents.peek().equals(ATOM_ENTRY)) {
					// entry title
					feedItem.setTitle(parser.getText());
				}
				parents.push(copy);
			} else if (parents.peek().equals(ATOM_SUMMARY)) {
				String copy = parents.pop();
				if (parents.peek().equals(ATOM_ENTRY)) {
					currentItemHasSummary = true;
					feedItem.setDescription(parser.getText());
				}
				parents.push(copy);
			} else if (parents.peek().equals(ATOM_CONTENT)) {
				if (!currentItemHasSummary
						&& (parents.peek().equals(ATOM_ENTRY))) {
					feedItem.setDescription(parser.getText());
				}
			} else if (parents.peek().equals(ATOM_PUBLISHED)) {
				String copy = parents.pop();
				if (parents.peek().equals(ATOM_ENTRY)) {
					try {
						feedItem.setDate(parseDate(parser.getText()));
					} catch (IndexOutOfBoundsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				parents.push(copy);
			} else if (parents.peek().equals(ATOM_SUBTITLE)) {
				feed.setDescription(parser.getText());
			}
		}

		private void onEndTag() {
			String fullName = parents.pop();
			if (fullName.equals(ATOM_ENTRY)) {
				feed.getItems().add(feedItem);
				feedItem = null;
			}
		}

		private Date parseDate(String datestring)
				throws java.text.ParseException, IndexOutOfBoundsException {
			// original version by Chad Okere (ceothrow1 at gmail dotcom)
			// http://cokere.com/RFC3339Date.txt

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
	}
}
