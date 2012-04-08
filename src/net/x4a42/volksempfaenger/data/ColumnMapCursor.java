package net.x4a42.volksempfaenger.data;

import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.database.CursorWrapper;

public class ColumnMapCursor extends CursorWrapper {

	private String[] columns;

	private int[] map;

	public ColumnMapCursor(Cursor cursor, String[] from, String[] to) {
		super(cursor);
		assert (from.length == to.length);
		columns = from;
		map = new int[columns.length];
		for (int i = 0; i < map.length; i++) {
			map[i] = cursor.getColumnIndex(to[i]);
		}
	}

	@Override
	public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
		super.copyStringToBuffer(map[columnIndex], buffer);
	}

	@Override
	public byte[] getBlob(int columnIndex) {
		return super.getBlob(map[columnIndex]);
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public int getColumnIndex(String columnName) {
		try {
			return getColumnIndexOrThrow(columnName);
		} catch (IllegalArgumentException e) {
			return -1;
		}
	}

	@Override
	public int getColumnIndexOrThrow(String columnName)
			throws IllegalArgumentException {
		for (int i = 0; i < columns.length; i++) {
			if (columns[i].equals(columnName)) {
				return i;
			}
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String getColumnName(int columnIndex) {
		try {
			return columns[columnIndex];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	@Override
	public String[] getColumnNames() {
		return columns.clone();
	}

	@Override
	public double getDouble(int columnIndex) {
		return super.getDouble(map[columnIndex]);
	}

	@Override
	public float getFloat(int columnIndex) {
		return super.getFloat(map[columnIndex]);
	}

	@Override
	public int getInt(int columnIndex) {
		return super.getInt(map[columnIndex]);
	}

	@Override
	public long getLong(int columnIndex) {
		return super.getLong(map[columnIndex]);
	}

	@Override
	public short getShort(int columnIndex) {
		return super.getShort(map[columnIndex]);
	}

	@Override
	public String getString(int columnIndex) {
		return super.getString(map[columnIndex]);
	}

	@Override
	public int getType(int columnIndex) {
		return super.getType(map[columnIndex]);
	}

	@Override
	public boolean isNull(int columnIndex) {
		return super.isNull(map[columnIndex]);
	}

}
