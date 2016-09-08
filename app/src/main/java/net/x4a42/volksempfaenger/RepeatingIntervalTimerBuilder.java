package net.x4a42.volksempfaenger;

import android.os.Handler;

public class RepeatingIntervalTimerBuilder
{
    public RepeatingIntervalTimer build(int delayMillis)
    {
        return new RepeatingIntervalTimer(new Handler(), delayMillis);
    }
}
