package net.x4a42.volksempfaenger.feedparser;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FeedParser {

	public static Feed parse(Reader reader) throws FeedParserException,
			IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		FeedHandler handler = new FeedHandler();
		try {
			parser = factory.newSAXParser();
			parser.parse(new InputSource(reader), handler);
			if (!handler.isFeed) {
				throw new NotAFeedException();
			}
			Feed feed = handler.feed;
			for (FeedItem item : feed.getItems()) {
				if (item.getDate() == null) {
					throw new NotAFeedException();
				}
			}
			Collections.sort(feed.getItems(), new Comparator<FeedItem>() {
				@Override
				public int compare(FeedItem lhs, FeedItem rhs) {
					return rhs.getDate().compareTo(lhs.getDate());
				}
			});
			return feed;
		} catch (ParserConfigurationException e) {
			throw new FeedParserException(e);
		} catch (SAXException e) {
			throw new FeedParserException(e);
		} catch (NullPointerException e) {
			throw new FeedParserException("NullPointerException inside Parser",
					e);
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

		public final Feed feed = new Feed();
		public boolean isFeed = false;

		private FeedItem feedItem = null;
		private final Stack<Tag> parents = new Stack<Tag>();
		private boolean skipMode = false;
		private boolean xhtmlMode = false;
		private boolean currentRssItemHasHtml = false;
		private boolean currentItemHasITunesSummaryAlternative = false;
		private boolean currentAtomItemHasPublished = false;
		private boolean hasITunesImage = false;
		private int skipDepth = 0;
		private final StringBuilder buffer = new StringBuilder();

		private static final String ATOM_ATTR_HREF = "href";
		private static final String ATOM_ATTR_REL = "rel";
		private static final String ATOM_ATTR_TYPE = "type";
		private static final String ATOM_ATTR_LENGTH = "length";
		private static final String ATOM_ATTR_TITLE = "title";

		private static final String RSS_ATTR_URL = "url";
		private static final String RSS_ATTR_TYPE = "type";
		private static final String RSS_ATTR_LENGTH = "length";

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
						isFeed = true;
					}
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
				String relString = atts.getValue(ATOM_ATTR_REL);
				AtomRel rel = AtomRel.UNKNOWN;
				if (relString != null) {
					rel = getAtomRel(relString);
					relString = null;
				}
				switch (rel) {
				case ENCLOSURE:
					if (parents.peek() == Tag.ATOM_ENTRY) {
						Enclosure enclosure = new Enclosure();
						enclosure.setFeedItem(feedItem);
						enclosure.setUrl(atts.getValue(ATOM_ATTR_HREF));
						enclosure.setMime(atts.getValue(ATOM_ATTR_TYPE));
						enclosure.setTitle(atts.getValue(ATOM_ATTR_TITLE));

						String length = atts.getValue(ATOM_ATTR_LENGTH);
						if (length != null && length.length() > 0) {
							enclosure.setSize(Long.parseLong(length.trim()));
						}
						feedItem.getEnclosures().add(enclosure);
					}
					break;
				case ALTERNATE:
					String mimeString = atts.getValue(ATOM_ATTR_TYPE);
					Mime type = Mime.UNKNOWN;
					if (mimeString != null) {
						type = getMime(mimeString);
						mimeString = null;
					}
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
					if (length != null && length.length() > 0) {
						enclosure.setSize(Long.parseLong(length.trim()));
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
						feedItem.setDate(parseAtomDate(buffer.toString()));
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
						feedItem.setDate(parseAtomDate(buffer.toString()));
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
					try {
						feedItem.setDate(parseRssDate(buffer.toString()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
			datestring = datestring.trim().toUpperCase();
			// dirty version - write a new one TODO
			// Modified version of http://cokere.com/RFC3339Date.txt
			/*
			 * I was working on an Atom (http://www.w3.org/2005/Atom) parser and
			 * discovered that I could not parse dates in the format defined by
			 * RFC 3339 using the SimpleDateFormat class. The reason was the ':'
			 * in the time zone. This code strips out the colon if it's there
			 * and tries four different formats on the resulting string
			 * depending on if it has a time zone, or if it has a fractional
			 * second part. There is a probably a better way to do this, and a
			 * more proper way. But this is a really small addition to a
			 * codebase (You don't need a jar, just throw this function in some
			 * static Utility class if you have one).
			 * 
			 * Feel free to use this in your code, but I'd appreciate it if you
			 * keep this note in the code if you distribute it. Thanks!
			 * 
			 * For people who might be googling: The date format parsed by this
			 * goes by: atomDateConstruct, xsd:dateTime, RFC3339 and is
			 * compatable with: ISO.8601.1988, W3C.NOTE-datetime-19980827 and
			 * W3C.REC-xmlschema-2-20041028 (that I know of)
			 * 
			 * 
			 * Copyright 2007, Chad Okere (ceothrow1 at gmail dotcom) OMG NO
			 * WARRENTY EXPRESSED OR IMPLIED!!!1
			 */

			// if there is no time zone, we don't need to do any special
			// parsing.
			if (datestring.charAt(datestring.length() - 1) == 'Z') {
				try {
					// spec for RFC3339
					return formats[8].parse(datestring);
				} catch (java.text.ParseException pe) {
					// try again with optional decimals
					// spec for RFC3339 (with fractional seconds)
					return formats[9].parse(datestring);
				}
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
			try {
				return formats[10].parse(datestring);// spec for RFC3339
			} catch (java.text.ParseException pe) {
				// try again with optional decimals
				// spec for RFC3339 (with fractional seconds)
				return formats[11].parse(datestring);
			}
		}

		private static final SimpleDateFormat formats[] = new SimpleDateFormat[] {
				new SimpleDateFormat("EEE, d MMM yy HH:mm:ss z", Locale.US),
				new SimpleDateFormat("EEE, d MMM yy HH:mm z", Locale.US),
				new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US),
				new SimpleDateFormat("EEE, d MMM yyyy HH:mm z", Locale.US),
				new SimpleDateFormat("d MMM yy HH:mm z", Locale.US),
				new SimpleDateFormat("d MMM yy HH:mm:ss z", Locale.US),
				new SimpleDateFormat("d MMM yyyy HH:mm z", Locale.US),
				new SimpleDateFormat("d MMM yyyy HH:mm:ss z", Locale.US),
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"),
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"),
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"),
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ") };

		static {
			formats[9].setLenient(true);
			formats[11].setLenient(true);
		}

		private Date parseRssDate(String datestring) throws ParseException {
			// dirty version - write a new one TODO
			datestring = datestring.trim();
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
			return format.parse(datestring);
		}

		private static Namespace getNamespace(String nsString) {
			return lookupString(namespaces, nsString, Namespace.UNKNOWN);
		}

		private static Tag getTag(Namespace ns, String tagString) {
			HashMap<String, Tag> map = null;
			switch (ns) {
			case ATOM:
				map = atomTags;
				break;
			case RSS:
			case NONE:
				map = rssTags;
				break;
			case RSS_CONTENT:
				if (tagString.equals("encoded")) {
					return Tag.RSS_CONTENT_ENCODED;
				} else {
					return Tag.UNKNOWN;
				}
			case ITUNES:
				map = itunesTags;
				break;
			}
			if (map == null) {
				return Tag.UNKNOWN;
			} else {
				return lookupString(map, tagString, Tag.UNKNOWN);
			}
		}

		private static AtomRel getAtomRel(String relString) {
			return lookupString(atomRels, relString, AtomRel.UNKNOWN);
		}

		private static Mime getMime(String mimeString) {
			return lookupString(mimes, mimeString, Mime.UNKNOWN);
		}

		private static <V> V lookupString(HashMap<String, V> map, String key,
				V unknown) {
			V value = map.get(key);
			if (value == null) {
				return unknown;
			} else {
				return value;
			}
		}

		// load of HashMaps
		private static final float load = 0.75f;

		// The size of the HashMaps is set to (number_of_elements / load) + 1 so
		// no re-hashing is necessary. Update the number_of_elements when new
		// elements are added.
		private final static HashMap<String, Namespace> namespaces = new HashMap<String, Namespace>(
				(int) (6 / load) + 1, load);
		static {
			namespaces.put("http://www.w3.org/2005/Atom", Namespace.ATOM);
			namespaces.put("http://backend.userland.com/RSS2", Namespace.RSS);
			namespaces.put("http://purl.org/rss/1.0/modules/content/",
					Namespace.RSS_CONTENT);
			namespaces.put("http://www.w3.org/1999/xhtml", Namespace.XHTML);
			namespaces.put("http://www.itunes.com/dtds/podcast-1.0.dtd",
					Namespace.ITUNES);
			namespaces.put("", Namespace.NONE);
		}
		private final static HashMap<String, Tag> atomTags = new HashMap<String, Tag>(
				(int) (10 / load) + 1, load);
		static {
			atomTags.put("feed", Tag.ATOM_FEED);
			atomTags.put("title", Tag.ATOM_TITLE);
			atomTags.put("entry", Tag.ATOM_ENTRY);
			atomTags.put("link", Tag.ATOM_LINK);
			atomTags.put("content", Tag.ATOM_CONTENT);
			atomTags.put("published", Tag.ATOM_PUBLISHED);
			atomTags.put("updated", Tag.ATOM_UPDATED);
			atomTags.put("subtitle", Tag.ATOM_SUBTITLE);
			atomTags.put("id", Tag.ATOM_ID);
			atomTags.put("icon", Tag.ATOM_ICON);
		}
		private final static HashMap<String, Tag> rssTags = new HashMap<String, Tag>(
				(int) (11 / load) + 1, load);
		static {
			rssTags.put("rss", Tag.RSS_TOPLEVEL);
			rssTags.put("channel", Tag.RSS_CHANNEL);
			rssTags.put("item", Tag.RSS_ITEM);
			rssTags.put("title", Tag.RSS_TITLE);
			rssTags.put("link", Tag.RSS_LINK);
			rssTags.put("description", Tag.RSS_DESCRIPTION);
			rssTags.put("enclosure", Tag.RSS_ENCLOSURE);
			rssTags.put("pubDate", Tag.RSS_PUB_DATE);
			rssTags.put("guid", Tag.RSS_GUID);
			rssTags.put("image", Tag.RSS_IMAGE);
			rssTags.put("url", Tag.RSS_URL);
		}
		private final static HashMap<String, Tag> itunesTags = new HashMap<String, Tag>(
				(int) (2 / load) + 1, load);
		static {
			itunesTags.put("image", Tag.ITUNES_IMAGE);
			itunesTags.put("summary", Tag.ITUNES_SUMMARY);
		}
		private final static HashMap<String, AtomRel> atomRels = new HashMap<String, AtomRel>(
				(int) (3 / load) + 1, load);
		static {
			atomRels.put("enclosure", AtomRel.ENCLOSURE);
			atomRels.put("alternate", AtomRel.ALTERNATE);
			atomRels.put("self", AtomRel.SELF);
		}
		private final static HashMap<String, Mime> mimes = new HashMap<String, Mime>(
				(int) (2 / load) + 1, load);
		static {
			mimes.put("text/html", Mime.HTML);
			mimes.put("text/xhtml", Mime.XHTML);
		}
	}
}
