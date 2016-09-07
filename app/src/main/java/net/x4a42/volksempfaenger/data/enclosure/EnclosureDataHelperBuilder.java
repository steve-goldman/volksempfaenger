package net.x4a42.volksempfaenger.data.enclosure;

import android.content.Context;

import net.x4a42.volksempfaenger.data.EnclosureCursorFactory;

public class EnclosureDataHelperBuilder
{
    public EnclosureDataHelper build(Context context)
    {
        return new EnclosureDataHelper(context.getContentResolver(),
                                       new EnclosureCursorFactory(),
                                       new EnclosureMetadata());
    }
}
