package net.x4a42.volksempfaenger.misc;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BatteryStatusBuilder
{
    public BatteryStatus build(Context context)
    {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent       intent       = context.registerReceiver(null, intentFilter);

        return new BatteryStatus(intent);
    }
}
