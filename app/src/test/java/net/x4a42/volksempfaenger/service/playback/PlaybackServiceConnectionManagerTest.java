package net.x4a42.volksempfaenger.service.playback;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PlaybackServiceConnectionManagerTest
{
    @Mock Context                          context;
    @Mock PlaybackServiceIntentProvider    intentProvider;
    @Mock Intent                           intent;
    @Mock PlaybackServiceFacade            facade;
    @Mock PlaybackServiceBinder            binder;
    @Mock ComponentName                    componentName;
    
    PlaybackServiceConnectionManager.Listener listener
            = Mockito.mock(PlaybackServiceConnectionManager.Listener.class);
    
    PlaybackServiceConnectionManager       connectionManager;
    
    @Before
    public void setUp() throws Exception
    {
        Mockito.when(intentProvider.getBindIntent()).thenReturn(intent);
        Mockito.when(binder.getFacade()).thenReturn(facade);
        
        connectionManager = new PlaybackServiceConnectionManager(context,
                                                                 intentProvider)
                .setListener(listener);
    }

    @Test
    public void onnCreate() throws Exception
    {
        connectionManager.onCreate();

        Mockito.verify(context).bindService(intent,
                                            connectionManager,
                                            Context.BIND_AUTO_CREATE);
    }

    @Test
    public void onnDestroy() throws Exception
    {
        connectionManager.onDestroy();

        Mockito.verify(context).unbindService(connectionManager);
    }

    @Test
    public void onnServiceConnected() throws Exception
    {
        connectionManager.onServiceConnected(componentName, binder);

        Mockito.verify(listener).onPlaybackServiceConnected();
        Assert.assertEquals(facade, connectionManager.getFacade());
    }

    @Test
    public void onnServiceDisconnected() throws Exception
    {
        connectionManager.onServiceConnected(componentName, binder);
        connectionManager.onServiceDisconnected(componentName);

        Mockito.verify(listener).onPlaybackServiceDisconnected();
        Assert.assertNull(connectionManager.getFacade());
    }
}
