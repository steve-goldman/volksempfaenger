package net.x4a42.volksempfaenger.misc;

import android.text.format.DateUtils;

public class DateFormatter
{
    public String format(long millis)
    {
        return "" + DateUtils.getRelativeTimeSpanString(millis);
    }
}
