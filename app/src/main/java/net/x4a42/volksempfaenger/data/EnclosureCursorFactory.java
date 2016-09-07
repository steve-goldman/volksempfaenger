package net.x4a42.volksempfaenger.data;

import android.database.Cursor;

public class EnclosureCursorFactory
{
    public EnclosureCursor create(Cursor cursor)
    {
        return new EnclosureCursor(cursor);
    }
}
