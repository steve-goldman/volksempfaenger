package net.x4a42.volksempfaenger;

import android.os.Handler;

public class RepeatingIntervalTimer implements Runnable
{
    private final Handler handler;
    private final long    delayMillis;
    private Runnable      runnable;

    public RepeatingIntervalTimer(Handler handler, int delayMillis)
    {
        this.handler     = handler;
        this.delayMillis = delayMillis;
    }

    public RepeatingIntervalTimer setRunnable(Runnable runnable)
    {
        this.runnable = runnable;
        return this;
    }

    public void start()
    {
        handler.post(this);
    }

    public void stop()
    {
        handler.removeCallbacks(this);
    }

    @Override
    public void run()
    {
        runnable.run();
        handler.postDelayed(this, delayMillis);
    }
}
