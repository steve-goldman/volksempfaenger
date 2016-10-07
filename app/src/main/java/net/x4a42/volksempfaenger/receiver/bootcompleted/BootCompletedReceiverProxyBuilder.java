package net.x4a42.volksempfaenger.receiver.bootcompleted;

import android.content.Context;

import net.x4a42.volksempfaenger.alarm.SyncAllAlarmManager;
import net.x4a42.volksempfaenger.alarm.SyncAllAlarmManagerBuilder;

class BootCompletedReceiverProxyBuilder
{
    public BootCompletedReceiverProxy build(Context context)
    {
        SyncAllAlarmManager alarmManager
                = new SyncAllAlarmManagerBuilder().build(context);

        return new BootCompletedReceiverProxy(alarmManager);
    }
}
