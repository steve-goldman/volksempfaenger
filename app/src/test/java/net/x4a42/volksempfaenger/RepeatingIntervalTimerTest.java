package net.x4a42.volksempfaenger;

import android.os.Handler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class RepeatingIntervalTimerTest
{
    @Mock Handler                handler;
    @Mock Runnable               runnable;
    int                          delayMillis = 1000;
    RepeatingIntervalTimer       timer;

    @Before
    public void setUp() throws Exception
    {
        timer = new RepeatingIntervalTimer(handler, delayMillis)
                .setRunnable(runnable);
    }

    @Test
    public void start() throws Exception
    {
        timer.start();

        Mockito.verify(handler).post(timer);
    }

    @Test
    public void stop() throws Exception
    {
        timer.stop();

        Mockito.verify(handler).removeCallbacks(timer);
    }

    @Test
    public void run() throws Exception
    {
        timer.run();

        InOrder inOrder = Mockito.inOrder(runnable, handler);
        inOrder.verify(runnable).run();
        inOrder.verify(handler).postDelayed(timer, delayMillis);
    }
}
