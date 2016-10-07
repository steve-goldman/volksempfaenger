package net.x4a42.volksempfaenger.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.Preferences;

public class SyncAllAlarmManager
{
    private final AlarmManager  alarmManager;
    private final Preferences   preferences;
    private final PendingIntent pendingIntent;

    public SyncAllAlarmManager(AlarmManager  alarmManager,
                               Preferences   preferences,
                               PendingIntent pendingIntent)
    {
        this.alarmManager  = alarmManager;
        this.preferences   = preferences;
        this.pendingIntent = pendingIntent;
    }

    public void scheduleIfFirstTime()
    {
        if (!preferences.isSyncAlarmScheduled())
        {
            reschedule();
        }
    }

    public void reschedule()
    {
        long interval = preferences.getSyncInterval();
        if (interval == 0)
        {
            Log.d(this, "canceling sync all alarm");
            alarmManager.cancel(pendingIntent);
            return;
        }

        Log.d(this, String.format("scheduling sync all alarm:%d ms", interval));
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                                         interval,
                                         interval,
                                         pendingIntent);
        preferences.setSyncAlarmScheduled();
    }
}
