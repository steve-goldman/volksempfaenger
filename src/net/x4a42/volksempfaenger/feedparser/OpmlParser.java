package net.x4a42.volksempfaenger.feedparser;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OpmlParser {

	public static List<String> parse(Reader reader) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		OpmlHandler handler = new OpmlHandler();

		try {
			parser = factory.newSAXParser();
			parser.parse(new InputSource(reader), handler);
			return handler.getUrls();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static class OpmlHandler extends DefaultHandler {
		private LinkedList<String> urls = new LinkedList<String>();

		private final static String OPML_OUTLINE = "outline";
		private final static String OPML_XML_URL = "xmlUrl";

		public List<String> getUrls() {
			return urls;
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes atts) {
			if (!uri.isEmpty()) {
				// opml does not have a namespace
				// ignore all elements which have one
				return;
			}

			if (localName == OPML_OUTLINE) {
				String url = atts.getValue(OPML_XML_URL);
				if (url != null) {
					urls.add(url);
				}
			}
		}
	}

}
