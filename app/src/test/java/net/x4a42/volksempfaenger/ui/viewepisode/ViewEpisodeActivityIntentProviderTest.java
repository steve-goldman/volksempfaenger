package net.x4a42.volksempfaenger.ui.viewepisode;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import junit.framework.Assert;

import net.x4a42.volksempfaenger.IntentBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ViewEpisodeActivityIntentProviderTest
{
    @Mock Uri           episodeUri;
    @Mock Context       context;
    @Mock IntentBuilder intentBuilder;
    @Mock Intent        intent;

    ViewEpisodeActivityIntentProvider intentProvider;

    @Before
    public void setup() throws Exception
    {
        Mockito.when(intentBuilder.build(context, ViewEpisodeActivity.class)).thenReturn(intent);
        Mockito.when(intent.setData(episodeUri)).thenReturn(intent);
        intentProvider = new ViewEpisodeActivityIntentProvider(context, intentBuilder);
    }
    @Test
    public void testGetIntent() throws Exception
    {
        Intent createdIntent = intentProvider.getIntent(episodeUri);

        Mockito.verify(intent).setData(episodeUri);
        Assert.assertEquals(intent, createdIntent);
    }
}
