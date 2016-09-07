package net.x4a42.volksempfaenger;

import android.app.Activity;
import android.support.v4.app.NavUtils;

public class NavUtilsWrapper
{
    private final Activity activity;

    public NavUtilsWrapper(Activity activity)
    {
        this.activity = activity;
    }

    public void navigateUpFromSameTask()
    {
        NavUtils.navigateUpFromSameTask(activity);
    }
}
