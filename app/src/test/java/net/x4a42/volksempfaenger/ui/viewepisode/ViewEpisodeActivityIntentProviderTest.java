package net.x4a42.volksempfaenger.ui.viewepisode;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import junit.framework.Assert;

import net.x4a42.volksempfaenger.IntentBuilder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class ViewEpisodeActivityIntentProviderTest
{
    Uri           episodeUri    = Mockito.mock(Uri.class);
    Context       context       = Mockito.mock(Context.class);
    IntentBuilder intentBuilder = Mockito.mock(IntentBuilder.class);
    Intent        intent        = Mockito.mock(Intent.class);

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