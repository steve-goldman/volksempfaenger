package net.x4a42.volksempfaenger.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

public class ExternalStorageHelper {

	private Context context;

	private enum StorageState {
		UNAVAILABLE, READONLY, WRITEABLE
	}

	public ExternalStorageHelper(Context context) {
		this.context = context;
	}

	public void assertExternalStorageReadable() {
		assertExternalStorageReadable(context);
	}

	public static void assertExternalStorageReadable(Context context) {
		if (getExternalStorageState() == StorageState.UNAVAILABLE) {
			startActivity(context);
		}
	}

	public void assertExternalStorageWritable() {
		assertExternalStorageWritable(context);
	}

	public static void assertExternalStorageWritable(Context context) {
		if (getExternalStorageState() != StorageState.WRITEABLE) {
			startActivity(context);
		}
	}

	private static StorageState getExternalStorageState() {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return StorageState.WRITEABLE;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return StorageState.READONLY;
		} else {
			return StorageState.UNAVAILABLE;
		}
	}

	private static void startActivity(Context context) {
		context.startActivity(new Intent(context,
				ExternalStorageMissingActivity.class));
	}

}
