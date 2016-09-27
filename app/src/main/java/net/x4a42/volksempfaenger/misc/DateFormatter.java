package net.x4a42.volksempfaenger.misc;

import java.text.DateFormat;
import java.util.Date;

public class DateFormatter
{
    public String format(long millis)
    {
        return DateFormat.getDateInstance().format(new Date(millis));
    }
}
