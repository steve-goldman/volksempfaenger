package net.x4a42.volksempfaenger.misc;

import java.util.Map;

import android.graphics.Bitmap;
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
		Map<Long, Bitmap> snapshot = snapshot();
		while (size() > maxSize) {
			for (Long key : snapshot.keySet()) {
				remove(key);
			}
		}
	}
}
