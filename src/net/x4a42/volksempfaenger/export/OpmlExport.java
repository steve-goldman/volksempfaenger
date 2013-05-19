package net.x4a42.volksempfaenger.export;

import android.util.Xml;
import net.x4a42.volksempfaenger.data.PodcastCursor;
import org.xmlpull.v1.XmlSerializer;

import java.io.*;


public class OpmlExport {

	public void export(PodcastCursor cursor, File destination) throws IOException {
		XmlSerializer serializer = Xml.newSerializer();
		FileOutputStream fos = new FileOutputStream(destination);
		serializer.setOutput(fos, "UTF-8");

		serializer.startDocument("UTF-8", false);

		serializer.startTag(null, OpmlConstants.OPML);

		serializer.attribute(null, OpmlConstants.VERSION, "2.0");

		//write head
		serializer.startTag(null, OpmlConstants.HEAD);
		serializer.startTag(null, OpmlConstants.TITLE);
		serializer.text("holopod subscription export");
		serializer.endTag(null, OpmlConstants.TITLE);
		serializer.endTag(null, OpmlConstants.HEAD);

		serializer.startTag(null, OpmlConstants.BODY);

		if (cursor.moveToFirst()) {
			do {
				serializer.startTag(null, OpmlConstants.OUTLINE);

				serializer.attribute(null, OpmlConstants.TITLE, cursor.getTitle());
				serializer.attribute(null, OpmlConstants.TEXT, cursor.getDescription());
				serializer.attribute(null, OpmlConstants.HTMLURL, cursor.getWebsite());
				serializer.attribute(null, OpmlConstants.XMLURL, cursor.getFeed());

				serializer.endTag(null, OpmlConstants.OUTLINE);
			} while (cursor.moveToNext());
		}

		serializer.endTag(null, OpmlConstants.BODY);
		serializer.endTag(null, OpmlConstants.OPML);
		serializer.endDocument();
		fos.flush();
		fos.close();
	}
}
