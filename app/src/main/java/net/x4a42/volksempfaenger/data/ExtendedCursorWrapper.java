package net.x4a42.volksempfaenger.data;

import android.database.Cursor;
import android.database.CursorWrapper;

public class ExtendedCursorWrapper extends CursorWrapper {

	public ExtendedCursorWrapper(Cursor cursor) {
		super(cursor);
	}

	public String rowToString() {
		if (isBeforeFirst()) {
			return getClass().getName() + "[isBeforeFirst()=true]";
		} else if (isAfterLast()) {
			return getClass().getName() + "[isAfterLast()=true]";
		}

		String string = getClass().getName();
		string += "[";

		for (int i = 0; i < getColumnCount(); i++) {
			if (!isNull(i)) {
				if (i != 0) {
					string += ", ";
				}
				string += getColumnName(i);
				string += "=";
				string += getString(i);
			}
		}

		string += "]";
		return string;
	}

}
