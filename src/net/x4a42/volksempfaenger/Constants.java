package net.x4a42.volksempfaenger;

import android.net.Uri;

public final class Constants {

	// Intents
	public static final String ACTION_OI_PICK_FILE = "org.openintents.action.PICK_FILE";

	// Extras
	public static final String EXTRA_OI_TITLE = "org.openintents.extra.TITLE";

	public static final String URL_WEBSITE = "http://volksempfaenger.0x4a42.net/";
	public static final String[] FEEDBACK_TO = { "volksempfaenger@0x4a42.net" };
	public static final Uri OI_FILEMANGER_URI_PLAY = Uri
			.parse("market://details?id=org.openintents.filemanager");
	public static final Uri OI_FILEMANGER_URI_HTTP = Uri
			.parse("http://openintents.org/en/filemanager");

	// UI
	public static final int PREVIEW_LOGO_SIZE = 64;

	// other
	public static final String ERROR_REPORT_PREFIX = "error-report";
}
