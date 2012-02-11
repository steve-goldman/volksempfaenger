package net.x4a42.volksempfaenger.data;

import java.util.Arrays;
import java.util.Comparator;

import net.x4a42.volksempfaenger.data.Columns.Episode;
import android.database.Cursor;
import android.database.CursorWrapper;

public class SortByStatusCursor extends CursorWrapper implements Cursor {
	private int current_position = 0;
	private Integer[] order;
	private Cursor cursor;

	public SortByStatusCursor(Cursor cursor) {
		super(cursor);
		this.cursor = cursor;
		order = new Integer[cursor.getCount()];

		final int[] status = new int[cursor.getCount()];
		cursor.moveToFirst();
		for (int i = 0; i < cursor.getCount(); i++) {
			order[i] = i;
			status[i] = cursor.getInt(cursor
					.getColumnIndex(Episode.DOWNLOAD_STATUS));
			cursor.moveToNext();
		}
		Arrays.sort(order, new Comparator<Integer>() {

			@Override
			public int compare(Integer lhs, Integer rhs) {
				return status[lhs.intValue()] - status[rhs.intValue()];
			}
		});
		moveToFirst();
	}

	@Override
	public boolean moveToPosition(int position) {
		if (position < 0 || position >= order.length) {
			return false;
		}
		boolean result = cursor.moveToPosition(order[position]);
		if (result) {
			current_position = position;
		}
		return result;
	}

	@Override
	public boolean move(int offset) {
		return moveToPosition(current_position + offset);
	}

	@Override
	public boolean moveToFirst() {
		return moveToPosition(0);
	}

	@Override
	public boolean moveToLast() {
		return moveToPosition(cursor.getCount() - 1);
	}

	@Override
	public boolean moveToNext() {
		return moveToPosition(current_position + 1);
	}

	@Override
	public boolean moveToPrevious() {
		return moveToPosition(current_position - 1);
	}

}
