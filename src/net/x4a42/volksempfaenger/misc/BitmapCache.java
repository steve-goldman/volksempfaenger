package net.x4a42.volksempfaenger.misc;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

public class BitmapCache extends LruCache<Long, Bitmap> {

	public BitmapCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(Long key, Bitmap value) {
		return value.getByteCount();
	}

	public void trimToSize(int maxSize) {
		for (Long key : snapshot().keySet()) {
			if (size() > maxSize) {
				break;
			}
			remove(key);
		}
	}

	@Override
	protected void entryRemoved(boolean evicted, Long key, Bitmap oldValue,
			Bitmap newValue) {
		Log.v("BitmapCache", "removed " + key);
	}
}
