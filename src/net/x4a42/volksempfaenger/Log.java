package net.x4a42.volksempfaenger;

public class Log {
	public static final int ASSERT = android.util.Log.ASSERT;
	public static final int DEBUG = android.util.Log.DEBUG;
	public static final int ERROR = android.util.Log.ERROR;
	public static final int INFO = android.util.Log.INFO;
	public static final int VERBOSE = android.util.Log.VERBOSE;
	public static final int WARN = android.util.Log.WARN;

	public static String getTag(Class<? extends Object> cls) {
		return cls == null ? "<null>" : cls.getSimpleName();
	}

	private static String getTag(Object obj) {
		return obj == null ? "<null>" : getTag(obj.getClass());
	}

	// Standard android.util.Log methods
	public static int d(String tag, String msg) {
		return BuildConfig.DEBUG ? android.util.Log.d(tag, msg) : 0;
	}

	public static int d(String tag, String msg, Throwable tr) {
		return BuildConfig.DEBUG ? android.util.Log.d(tag, msg, tr) : 0;
	}

	public static int e(String tag, String msg) {
		return android.util.Log.e(tag, msg);
	}

	public static int e(String tag, String msg, Throwable tr) {
		return android.util.Log.e(tag, msg, tr);
	}

	public static String getStackTraceString(Throwable tr) {
		return android.util.Log.getStackTraceString(tr);
	}

	public static int i(String tag, String msg) {
		return android.util.Log.i(tag, msg);
	}

	public static int i(String tag, String msg, Throwable tr) {
		return android.util.Log.i(tag, msg, tr);
	}

	public static boolean isLoggable(String tag, int level) {
		return android.util.Log.isLoggable(tag, level);
	}

	public static int println(int priority, String tag, String msg) {
		return android.util.Log.println(priority, tag, msg);
	}

	public static int v(String tag, String msg) {
		return android.util.Log.v(tag, msg);
	}

	public static int v(String tag, String msg, Throwable tr) {
		return android.util.Log.v(tag, msg, tr);
	}

	public static int w(String tag, Throwable tr) {
		return android.util.Log.w(tag, tr);
	}

	public static int w(String tag, String msg, Throwable tr) {
		return android.util.Log.w(tag, msg, tr);
	}

	public static int w(String tag, String msg) {
		return android.util.Log.w(tag, msg);
	}

	public static int wtf(String tag, Throwable tr) {
		return android.util.Log.wtf(tag, tr);
	}

	public static int wtf(String tag, String msg) {
		return android.util.Log.wtf(tag, msg);
	}

	public static int wtf(String tag, String msg, Throwable tr) {
		return android.util.Log.w(tag, msg, tr);
	}

	// Custom methods
	public static int d(Object tagObj, String msg) {
		return d(getTag(tagObj), msg);
	}

	public static int d(Object tagObj, String msg, Throwable tr) {
		return d(getTag(tagObj), msg, tr);
	}

	public static int e(Object tagObj, String msg) {
		return e(getTag(tagObj), msg);
	}

	public static int e(Object tagObj, String msg, Throwable tr) {
		return e(getTag(tagObj), msg, tr);
	}

	public static int i(Object tagObj, String msg) {
		return i(getTag(tagObj), msg);
	}

	public static int i(Object tagObj, String msg, Throwable tr) {
		return i(getTag(tagObj), msg, tr);
	}

	public static int println(int priority, Object tagObj, String msg) {
		return println(priority, getTag(tagObj), msg);
	}

	public static int v(Object tagObj, String msg) {
		return v(getTag(tagObj), msg);
	}

	public static int v(Object tagObj, String msg, Throwable tr) {
		return v(getTag(tagObj), msg, tr);
	}

	public static int w(Object tagObj, Throwable tr) {
		return w(getTag(tagObj), tr);
	}

	public static int w(Object tagObj, String msg, Throwable tr) {
		return w(getTag(tagObj), msg, tr);
	}

	public static int w(Object tagObj, String msg) {
		return w(getTag(tagObj), msg);
	}

	public static int wtf(Object tagObj, Throwable tr) {
		return wtf(getTag(tagObj), tr);
	}

	public static int wtf(Object tagObj, String msg) {
		return wtf(getTag(tagObj), msg);
	}

	public static int wtf(Object tagObj, String msg, Throwable tr) {
		return wtf(getTag(tagObj), msg, tr);
	}
}
