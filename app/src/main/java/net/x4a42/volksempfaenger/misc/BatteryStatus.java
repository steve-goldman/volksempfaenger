package net.x4a42.volksempfaenger.misc;

import android.content.Intent;
import android.os.BatteryManager;

public class BatteryStatus
{
    private final Intent intent;

    public BatteryStatus(Intent intent)
    {
        this.intent = intent;
    }

    public boolean isPluggedIn()
    {
        return getStatus() != 0;
    }

    private int getStatus()
    {
        return intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
    }
}
