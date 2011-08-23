package net.x4a42.volksempfaenger;

import java.io.File;
import java.util.Date;

import android.content.Context;
import android.os.Environment;

public class Utils {
	public static String joinArray(Object[] objects, CharSequence sep) {
		if (objects == null) {
			return null;
		}

		StringBuilder buf = new StringBuilder();

		int i = 0;
		while (i < objects.length) {
			if (objects[i] != null) {
				buf.append(objects[i]);
			}
			if (++i < objects.length) {
				buf.append(sep);
			}
		}

		return buf.toString();
	}

	public static long toUnixTimestamp(Date date) {
		return date.getTime() / 1000L;
	}

	public static String normalizeString(String string) {
		return string.replaceAll("\\s+", " ");
	}

	public static File joinPath(File base, String... children) {
		for (String child : children) {
			base = new File(base, child);
		}
		return base;
	}

	public static File joinPath(String base, String... children) {
		return joinPath(new File(base), children);
	}

	public static File getPodcastLogoFile(Context context, long podcastId) {
		return Utils.joinPath(Environment.getExternalStorageDirectory(),
				"Android", "data",
				VolksempfaengerApplication.getPackageInfo(context).packageName,
				"files", "logos", String.valueOf(podcastId));
	}
}
