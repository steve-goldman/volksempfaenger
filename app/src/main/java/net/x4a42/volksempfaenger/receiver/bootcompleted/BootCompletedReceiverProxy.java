package net.x4a42.volksempfaenger.receiver.bootcompleted;

import net.x4a42.volksempfaenger.alarm.SyncAllAlarmManager;

class BootCompletedReceiverProxy
{
    private final SyncAllAlarmManager alarmManager;

    public BootCompletedReceiverProxy(SyncAllAlarmManager alarmManager)
    {
        this.alarmManager = alarmManager;
    }

    public void onReceive()
    {
        alarmManager.reschedule();
    }
}
