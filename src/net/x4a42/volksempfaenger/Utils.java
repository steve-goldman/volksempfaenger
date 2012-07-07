package net.x4a42.volksempfaenger;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import net.x4a42.volksempfaenger.misc.BitmapCache;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;

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

	public static String joinArray(long[] longs, CharSequence sep) {
		if (longs == null) {
			return null;
		}

		String[] objects = new String[longs.length];

		for (int i = 0; i < longs.length; i++) {
			objects[i] = String.valueOf(longs[i]);
		}

		return joinArray(objects, sep);
	}

	public static long toUnixTimestamp(Date date) {
		return date.getTime() / 1000L;
	}

	public static String normalizeString(String string) {
		return string.replaceAll("\\s+", " ");
	}

	public static String normalizeFilename(String filename) {
		return filename.replaceAll("[^A-Za-z0-9-_\\.]+", "_");
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
		return Utils.joinPath(context.getExternalFilesDir(null), "logos",
				String.valueOf(podcastId));
	}

	public static Bitmap getPodcastLogoBitmap(Context context, long podcastId) {
		final BitmapCache cache = VolksempfaengerApplication.getCache();
		Bitmap bitmap = cache.get(podcastId);
		if (bitmap == null) {
			File podcastLogoFile = getPodcastLogoFile(context, podcastId);
			if (podcastLogoFile.isFile()) {
				bitmap = scalePodcastLogo(context,
						BitmapFactory.decodeFile(podcastLogoFile
								.getAbsolutePath()));
				cache.put(podcastId, bitmap);
			} else {
				return null;
			}
		}
		return bitmap;
	}

	public static File getDescriptionImageFile(Context context, String url) {
		return Utils.joinPath(context.getExternalCacheDir(), "images",
				sha1.hash(url));
	}

	public static File getEnclosureFile(Context context, long enclosureId,
			String filename) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String location = prefs.getString(PreferenceKeys.STORAGE_LOCATION,
				VolksempfaengerApplication.getDefaultStorageLocation());
		String fullFilename;
		if (filename == null) {
			fullFilename = String.valueOf(enclosureId);
		} else {
			fullFilename = new StringBuffer().append(enclosureId).append('_')
					.append(normalizeFilename(filename)).toString();
		}
		return new File(location, fullFilename);
	}

	public static class sha1 {
		// http://stackoverflow.com/questions/5980658/how-to-sha1-hash-a-string-in-android/5980789#5980789
		private static String convertToHex(byte[] data) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < data.length; i++) {
				int halfbyte = (data[i] >>> 4) & 0x0F;
				int two_halfs = 0;
				do {
					if ((0 <= halfbyte) && (halfbyte <= 9))
						buf.append((char) ('0' + halfbyte));
					else
						buf.append((char) ('a' + (halfbyte - 10)));
					halfbyte = data[i] & 0x0F;
				} while (two_halfs++ < 1);
			}
			return buf.toString();
		}

		public static String hash(String text) {
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("SHA-1");
			} catch (NoSuchAlgorithmException e) {
				Log.wtf(Log.getTag(Utils.class), e);
				return null;
			}
			byte[] sha1hash = new byte[40];
			try {
				md.update(text.getBytes("utf8"), 0, text.length());
			} catch (UnsupportedEncodingException e) {
				Log.w(Log.getTag(Utils.class), e);
				try {
					md.update(text.getBytes("iso-8859-1"), 0, text.length());
				} catch (UnsupportedEncodingException e1) {
					Log.wtf(Log.getTag(Utils.class), e);
					return null;
				}
			}
			sha1hash = md.digest();
			return convertToHex(sha1hash);
		}
	}

	public static boolean stringBoolean(String str) {
		return str.equals("true") || str.equals("yes");
	}

	public static String filenameFromUrl(String url) {
		int slashIndex = url.lastIndexOf("/");
		String filename = null;
		if (slashIndex != -1) {
			filename = url.substring(slashIndex + 1);
			if (filename.length() == 0) {
				filename = null;
			}
		}
		return filename;
	}

	public static void internStringArray(String[] strings) {
		for (int i = 0; i < strings.length; i++) {
			if (strings[i] != null) {
				strings[i] = strings[i].intern();
			}
		}
	}

	public static String hashContentValues(ContentValues values) {
		ArrayList<String> keys = new ArrayList<String>(values.keySet());
		Collections.sort(keys);
		StringBuilder b = new StringBuilder();
		for (String key : keys) {
			b.append(key);
			b.append((char) 0);
			b.append(values.getAsString(key));
			b.append((char) 0);
		}
		return sha1.hash(b.toString());
	}

	public static String formatTime(int milliseconds) {
		int seconds = milliseconds / 1000;
		int hours = seconds / 3600;
		int minutes = (seconds / 60) - (hours * 60);
		int seconds2 = seconds - (minutes * 60) - (hours * 3600);
		DecimalFormat format = new DecimalFormat("00");
		return format.format(hours) + ":" + format.format(minutes) + ":"
				+ format.format(seconds2);
	}

	public static String trimmedString(StringBuilder stringBuilder) {
		final int length = stringBuilder.length();
		int start, end;
		for (start = 0; start < length; start++) {
			if (!Character.isWhitespace(stringBuilder.charAt(start))) {
				break;
			}
		}
		for (end = length - 1; end >= start; end--) {
			if (!Character.isWhitespace(stringBuilder.charAt(end))) {
				break;
			}
		}
		return stringBuilder.substring(start, end + 1);
	}

	@TargetApi(16)
	@SuppressWarnings("deprecation")
	public static Notification notificationFromBuilder(Notification.Builder nb) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			return nb.getNotification();
		} else {
			return nb.build();
		}
	}

	public static Bitmap scalePodcastLogo(Context context, Bitmap logo) {
		int maxSize = 2 * context.getResources().getDimensionPixelSize(
				R.dimen.grid_column_width);
		Bitmap scaled = Bitmap.createScaledBitmap(logo, maxSize, maxSize, true);
		if (scaled != logo) {
			logo.recycle();
		}
		return scaled;
	}
}
