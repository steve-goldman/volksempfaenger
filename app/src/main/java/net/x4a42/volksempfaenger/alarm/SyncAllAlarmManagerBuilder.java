package net.x4a42.volksempfaenger.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import net.x4a42.volksempfaenger.PendingIntentBuilder;
import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.PreferencesBuilder;
import net.x4a42.volksempfaenger.service.syncall.SyncAllServiceIntentProviderBuilder;

public class SyncAllAlarmManagerBuilder
{
    public SyncAllAlarmManager build(Context context)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Preferences  preferences  = new PreferencesBuilder().build(context);

        Intent intent
                = new SyncAllServiceIntentProviderBuilder().build(context).getSyncIntent();

        PendingIntent pendingIntent
                = new PendingIntentBuilder().buildService(context, 0, intent, 0);

        return new SyncAllAlarmManager(alarmManager, preferences, pendingIntent);
    }
}
