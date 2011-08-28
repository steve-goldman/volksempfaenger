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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FeedParser {
	private final String TAG = getClass().getSimpleName();

	public static Feed parse(Reader reader) throws FeedParserException,
			IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		FeedHandler handler = new FeedHandler();
		try {
			parser = factory.newSAXParser();
			parser.parse(new InputSource(reader), handler);
			if (!handler.isFeed()) {
				throw new NotAFeedException();
			}
			Feed feed = handler.getFeed();
			for (FeedItem item : feed.getItems()) {
				if(item.getDate() == null) {
					throw new NotAFeedException();
				}
			}
			return feed;
		} catch (ParserConfigurationException e) {
			throw new FeedParserException();
		} catch (SAXException e) {
			throw new FeedParserException();
		} catch (NullPointerException e) {
			throw new FeedParserException("NullPointerException inside Parser");
		}
	}

	private static class FeedHandler extends DefaultHandler {
		private static enum Namespace {
			NONE, ATOM, RSS, RSS_CONTENT, UNKNOWN, XHTML, ITUNES
		}

		private static enum Tag {
			UNKNOWN, ATOM_FEED, ATOM_TITLE, ATOM_ENTRY, ATOM_LINK, ATOM_SUMMARY, ATOM_CONTENT, ATOM_PUBLISHED, ATOM_SUBTITLE, RSS_TOPLEVEL, RSS_CHANNEL, RSS_ITEM, RSS_TITLE, RSS_LINK, RSS_DESCRIPTION, RSS_ENCLOSURE, RSS_PUB_DATE, RSS_CONTENT_ENCODED, ATOM_ID, RSS_GUID, RSS_IMAGE, RSS_URL, ATOM_ICON, ITUNES_IMAGE, ITUNES_SUMMARY, ATOM_UPDATED
		}

		private static enum AtomRel {
			ENCLOSURE, ALTERNATE, SELF, UNKNOWN
		}

		private static enum Mime {
			HTML, XHTML, UNKNOWN
		}

		private static final String TAG = "ParserHelper";
		private Feed feed = new Feed();
		private FeedItem feedItem = null;
		private Stack<Tag> parents = new Stack<Tag>();
		private boolean isFeed = false;
		private boolean skipMode = false;
		private boolean xhtmlMode = false;
		private boolean currentRssItemHasHtml = false;
		private boolean currentItemHasITunesSummaryAlternative = false;
		private boolean currentAtomItemHasPublished = false;
		private boolean hasITunesImage = false;
		private int skipDepth = 0;
		private StringBuilder buffer = new StringBuilder();

		private static final String ATOM_ATTR_HREF = "href";
		private static final String ATOM_ATTR_REL = "rel";
		private static final String ATOM_ATTR_TYPE = "type";
		private static final String ATOM_ATTR_LENGTH = "length";
		private static final String ATOM_ATTR_TITLE = "title";

		private static final String RSS_ATTR_URL = "url";
		private static final String RSS_ATTR_TYPE = "type";
		private static final String RSS_ATTR_LENGTH = "length";

		public Feed getFeed() {
			return feed;
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			if (skipMode) {
				skipDepth++;
			} else {
				Namespace ns = getNamespace(uri);
				Tag tag = getTag(ns, localName);

				if (!isFeed) {
					// is current element one of the toplevel elements
					if (((ns == Namespace.ATOM) && tag == Tag.ATOM_FEED)
							|| ((ns == Namespace.NONE) && tag == Tag.RSS_TOPLEVEL)
							|| ((ns == Namespace.RSS) && tag == Tag.RSS_TOPLEVEL)) {

					}
					isFeed = true;
				}

				if (ns == Namespace.ATOM) {
					onStartTagAtom(tag, atts);
				} else if (ns == Namespace.NONE || ns == Namespace.RSS
						|| ns == Namespace.RSS_CONTENT) {
					onStartTagRss(tag, atts);
				} else if (ns == Namespace.XHTML && xhtmlMode) {
					onStartTagXHtml(localName, atts);
				} else if (ns == Namespace.ITUNES) {
					onStartTagITunes(tag, atts);
				} else {
					skipMode = true;
					skipDepth = 0;
					return;
				}
				parents.push(tag);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (skipMode) {
				return;
			}
			if (parents.peek() != Tag.UNKNOWN || xhtmlMode) {
				if (parents.peek() == Tag.RSS_DESCRIPTION
						&& currentRssItemHasHtml) {
					// we already have an HTML version of this, so just ignore
					// the plaintext
					return;
				}
				buffer.append(ch, start, length);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (skipMode) {
				if (skipDepth == 0) {
					skipMode = false;
				} else {
					skipDepth--;
				}
			} else {
				Namespace ns = getNamespace(uri);
				Tag tag = parents.pop();

				if (ns == Namespace.ATOM) {
					onEndTagAtom(tag);
				} else if (ns == Namespace.NONE || ns == Namespace.RSS
						|| ns == Namespace.RSS_CONTENT) {
					onEndTagRss(tag);
				} else if (ns == Namespace.XHTML && xhtmlMode) {
					onEndTagXHtml(localName);
				} else if (ns == Namespace.ITUNES) {
					onEndTagITunes(tag);
				}
				if (tag != Tag.UNKNOWN) {
					// clear buffer
					buffer.setLength(0);
				}
			}
		}

		private void onStartTagAtom(Tag tag, Attributes atts) {
			switch (tag) {
			case ATOM_ENTRY:
				feedItem = new FeedItem();
				feedItem.setFeed(feed);
				currentItemHasITunesSummaryAlternative = false;
				currentAtomItemHasPublished = false;
				break;
			case ATOM_CONTENT:
				if (atts.getValue(ATOM_ATTR_TYPE).equals("xhtml")) {
					xhtmlMode = true;
				}
			case ATOM_LINK:
				AtomRel rel = getAtomRel(atts.getValue(ATOM_ATTR_REL));
				switch (rel) {
				case ENCLOSURE:
					if (parents.peek() == Tag.ATOM_ENTRY) {
						Enclosure enclosure = new Enclosure();
						enclosure.setFeedItem(feedItem);
						enclosure.setUrl(atts.getValue(ATOM_ATTR_HREF));
						enclosure.setMime(atts.getValue(ATOM_ATTR_TYPE));
						enclosure.setTitle(atts.getValue(ATOM_ATTR_TITLE));

						String length = atts.getValue(ATOM_ATTR_LENGTH);
						if (length != null) {
							enclosure.setSize(Long.parseLong(length));
						}
						feedItem.getEnclosures().add(enclosure);
					}
					break;
				case ALTERNATE:
					Mime type = getMime(atts.getValue(ATOM_ATTR_TYPE));
					if (parents.peek() == Tag.ATOM_ENTRY) {
						if (type == Mime.UNKNOWN || type == Mime.HTML
								|| type == Mime.XHTML) {
							// actually there can be multiple
							// "alternate links"
							// this uses the LAST alternate link as the
							// URL for
							// the FeedItem
							feedItem.setUrl(atts.getValue(ATOM_ATTR_HREF));
						}
					} else if (parents.peek() == Tag.ATOM_FEED) {
						if (type == Mime.UNKNOWN || type == Mime.HTML
								|| type == Mime.XHTML) {
							// same issue as above with multiple
							// alternate links
							feed.setWebsite(atts.getValue(ATOM_ATTR_HREF));
						}
					}
					break;
				case SELF:
					if (parents.peek() == Tag.ATOM_FEED) {
						feed.setUrl(atts.getValue(ATOM_ATTR_HREF));
					}
					break;
				}
				break;
			}

		}

		private void onStartTagRss(Tag tag, Attributes atts) {
			switch (tag) {
			case RSS_ITEM:
				feedItem = new FeedItem();
				feedItem.setFeed(feed);
				currentRssItemHasHtml = false;
				currentItemHasITunesSummaryAlternative = false;
				break;
			case RSS_ENCLOSURE:
				if (parents.peek() == Tag.RSS_ITEM) {
					Enclosure enclosure = new Enclosure();
					enclosure.setFeedItem(feedItem);
					enclosure.setUrl(atts.getValue(RSS_ATTR_URL));
					enclosure.setMime(atts.getValue(RSS_ATTR_TYPE));

					String length = atts.getValue(RSS_ATTR_LENGTH);
					if (length != null) {
						enclosure.setSize(Long.parseLong(length));
					}
					feedItem.getEnclosures().add(enclosure);
				}
				break;
			}
		}

		private void onStartTagXHtml(String name, Attributes atts) {
			buffer.append("<");
			buffer.append(name);
			for (int i = 0; i < atts.getLength(); i++) {
				buffer.append(" ");
				buffer.append(atts.getLocalName(i));
				buffer.append("=\"");
				// escape double quotes (hope this works)
				buffer.append(atts.getValue(i).replaceAll("\"", "\\\""));
				buffer.append("\"");
			}
			buffer.append(">");
		}

		private void onStartTagITunes(Tag tag, Attributes atts) {
			if (tag == Tag.ITUNES_IMAGE
					&& (parents.peek() == Tag.RSS_CHANNEL || parents.peek() == Tag.ATOM_FEED)) {
				feed.setImage(atts.getValue("href"));
				hasITunesImage = true;
			}
		}

		private void onEndTagAtom(Tag tag) {
			switch (tag) {
			case ATOM_TITLE:
				if (parents.peek() == Tag.ATOM_FEED) {
					// feed title
					feed.setTitle(buffer.toString().trim());
				} else if (parents.peek() == Tag.ATOM_ENTRY) {
					// entry title
					feedItem.setTitle(buffer.toString().trim());
				}
				break;
			case ATOM_CONTENT:
				if (xhtmlMode) {
					xhtmlMode = false;
				}
				if (parents.peek() == Tag.ATOM_ENTRY) {
					feedItem.setDescription(buffer.toString().trim());
					currentItemHasITunesSummaryAlternative = true;
				}
				break;
			case ATOM_PUBLISHED:
				if (parents.peek() == Tag.ATOM_ENTRY) {
					try {
						feedItem.setDate(parseAtomDate(buffer.toString().trim()));
						currentAtomItemHasPublished = true;
					} catch (IndexOutOfBoundsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case ATOM_UPDATED:
				if (parents.peek() == Tag.ATOM_ENTRY
						&& !currentAtomItemHasPublished) {
					try {
						feedItem.setDate(parseAtomDate(buffer.toString().trim()));
					} catch (IndexOutOfBoundsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case ATOM_SUBTITLE:
				feed.setDescription(buffer.toString().trim());
				break;
			case ATOM_ENTRY:
				if (feedItem.getItemId() != null) {
					feed.getItems().add(feedItem);
				}
				feedItem = null;
				break;
			case ATOM_ID:
				if (parents.peek() == Tag.ATOM_ENTRY) {
					feedItem.setItemId(buffer.toString().trim());
				}
				break;
			case ATOM_ICON:
				if (parents.peek() == Tag.ATOM_FEED && !hasITunesImage) {
					feed.setImage(buffer.toString().trim());
				}
				break;
			}
		}

		private void onEndTagRss(Tag tag) {
			switch (tag) {
			case RSS_TITLE:
				switch (parents.peek()) {
				case RSS_CHANNEL:
					feed.setTitle(buffer.toString().trim());
					break;

				case RSS_ITEM:
					feedItem.setTitle(buffer.toString().trim());
					break;
				}
				break;
			case RSS_PUB_DATE:
				if (parents.peek() == Tag.RSS_ITEM) {
					feedItem.setDate(parseRssDate(buffer.toString().trim()));
				}
				break;
			case RSS_LINK:
				switch (parents.peek()) {
				case RSS_ITEM:
					feedItem.setUrl(buffer.toString().trim());
					break;
				case RSS_CHANNEL:
					feed.setWebsite(buffer.toString().trim());
					break;
				}
				break;
			case RSS_DESCRIPTION:
				if (!currentRssItemHasHtml) {
					switch (parents.peek()) {
					case RSS_ITEM:
						feedItem.setDescription(buffer.toString().trim());
						currentItemHasITunesSummaryAlternative = true;
						break;
					case RSS_CHANNEL:
						feed.setDescription(buffer.toString().trim());
						break;
					}
				}
				break;
			case RSS_CONTENT_ENCODED:
				currentRssItemHasHtml = true;
				switch (parents.peek()) {
				case RSS_ITEM:
					feedItem.setDescription(buffer.toString().trim());
					currentItemHasITunesSummaryAlternative = true;
					break;
				case RSS_CHANNEL:
					feed.setDescription(buffer.toString().trim());
					break;
				}
				break;
			case RSS_ITEM:
				if (feedItem.getItemId() != null) {
					feed.getItems().add(feedItem);
				}
				feedItem = null;
				currentRssItemHasHtml = false;
				break;
			case RSS_GUID:
				if (parents.peek() == Tag.RSS_ITEM) {
					feedItem.setItemId(buffer.toString().trim());
				}
				break;
			case RSS_URL:
				if (parents.peek() == Tag.RSS_IMAGE && !hasITunesImage) {
					Tag copy = parents.pop();
					if (parents.peek() == Tag.RSS_CHANNEL) {
						feed.setImage(buffer.toString().trim());
					}
					parents.push(copy);
				}
			}

		}

		private void onEndTagXHtml(String name) {
			buffer.append("</");
			buffer.append(name);
			buffer.append(">");
		}

		private void onEndTagITunes(Tag tag) {
			if (tag == Tag.ITUNES_SUMMARY
					&& (parents.peek() == Tag.ATOM_ENTRY || parents.peek() == Tag.RSS_ITEM)
					&& !currentItemHasITunesSummaryAlternative) {
				feedItem.setDescription(buffer.toString().trim());
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

		static final Map<String, Namespace> nsTable;
		static {
			Map<String, Namespace> temp = new HashMap<String, Namespace>();
			temp.put("http://www.w3.org/2005/Atom", Namespace.ATOM);
			temp.put("http://backend.userland.com/RSS2", Namespace.RSS);
			temp.put("http://purl.org/rss/1.0/modules/content/",
					Namespace.RSS_CONTENT);
			temp.put("http://www.w3.org/1999/xhtml", Namespace.XHTML);
			temp.put("http://www.itunes.com/dtds/podcast-1.0.dtd",
					Namespace.ITUNES);
			temp.put("", Namespace.NONE);
			nsTable = Collections.unmodifiableMap(temp);
		}

		private static Namespace getNamespace(String nsString) {
			Namespace ns = nsTable.get(nsString);

			if (ns == null) {
				ns = Namespace.UNKNOWN;
			}
			return ns;
		}

		static final Map<String, Tag> atomTable;
		static final Map<String, Tag> rssTable;
		static final Map<String, Tag> itunesTable;
		static {
			Map<String, Tag> temp = new HashMap<String, Tag>();
			temp.put("feed", Tag.ATOM_FEED);
			temp.put("title", Tag.ATOM_TITLE);
			temp.put("entry", Tag.ATOM_ENTRY);
			temp.put("link", Tag.ATOM_LINK);
			temp.put("content", Tag.ATOM_CONTENT);
			temp.put("published", Tag.ATOM_PUBLISHED);
			temp.put("updated", Tag.ATOM_UPDATED);
			temp.put("subtitle", Tag.ATOM_SUBTITLE);
			temp.put("id", Tag.ATOM_ID);
			temp.put("icon", Tag.ATOM_ICON);
			atomTable = Collections.unmodifiableMap(temp);
		}

		static {
			Map<String, Tag> temp = new HashMap<String, Tag>();
			temp.put("rss", Tag.RSS_TOPLEVEL);
			temp.put("channel", Tag.RSS_CHANNEL);
			temp.put("item", Tag.RSS_ITEM);
			temp.put("title", Tag.RSS_TITLE);
			temp.put("link", Tag.RSS_LINK);
			temp.put("description", Tag.RSS_DESCRIPTION);
			temp.put("enclosure", Tag.RSS_ENCLOSURE);
			temp.put("pubDate", Tag.RSS_PUB_DATE);
			temp.put("guid", Tag.RSS_GUID);
			temp.put("image", Tag.RSS_IMAGE);
			temp.put("url", Tag.RSS_URL);
			rssTable = Collections.unmodifiableMap(temp);
		}

		static {
			Map<String, Tag> temp = new HashMap<String, Tag>();
			temp.put("image", Tag.ITUNES_IMAGE);
			temp.put("summary", Tag.ITUNES_SUMMARY);
			itunesTable = Collections.unmodifiableMap(temp);
		}

		private static Tag getTag(Namespace ns, String tagString) {
			Tag tag = null;
			if (ns == Namespace.ATOM) {
				tag = atomTable.get(tagString);
			} else if (ns == Namespace.RSS || ns == Namespace.NONE) {
				tag = rssTable.get(tagString);
			} else if (ns == Namespace.RSS_CONTENT) {
				if (tagString.equals("encoded")) {
					tag = Tag.RSS_CONTENT_ENCODED;
				}
			} else if (ns == Namespace.ITUNES) {
				tag = itunesTable.get(tagString);
			}

			if (tag == null) {
				tag = Tag.UNKNOWN;
			}
			return tag;
		}

		static final Map<String, AtomRel> atomRelTable;
		static {
			Map<String, AtomRel> temp = new HashMap<String, AtomRel>();
			temp.put("enclosure", AtomRel.ENCLOSURE);
			temp.put("alternate", AtomRel.ALTERNATE);
			temp.put("self", AtomRel.SELF);
			atomRelTable = Collections.unmodifiableMap(temp);
		}

		private static AtomRel getAtomRel(String relString) {
			AtomRel rel = atomRelTable.get(relString);

			if (rel == null) {
				rel = AtomRel.UNKNOWN;
			}
			return rel;

		}

		static final Map<String, Mime> mimeTable;
		static {
			Map<String, Mime> temp = new HashMap<String, Mime>();
			temp.put("text/html", Mime.HTML);
			temp.put("text/xhtml", Mime.XHTML);
			mimeTable = Collections.unmodifiableMap(temp);
		}

		private static Mime getMime(String mimeString) {
			Mime mime = mimeTable.get(mimeString);

			if (mime == null) {
				mime = Mime.UNKNOWN;
			}
			return mime;

		}

		public boolean isFeed() {
			return isFeed;
		}
	}
}
