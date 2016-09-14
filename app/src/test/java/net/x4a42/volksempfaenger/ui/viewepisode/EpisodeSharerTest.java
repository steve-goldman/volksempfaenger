package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.Activity;
import android.content.Intent;

import net.x4a42.volksempfaenger.HtmlConverter;
import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.data.EpisodeCursor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class EpisodeSharerTest
{
    @Mock Activity      activity;
    @Mock IntentBuilder intentBuilder;
    @Mock Intent        intent;
    @Mock HtmlConverter converter;
    @Mock EpisodeCursor cursor;
    EpisodeSharer       sharer;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(intentBuilder.build(Intent.ACTION_SEND)).thenReturn(intent);

        sharer = new EpisodeSharer(activity, intentBuilder, converter);
    }

    @Test
    public void testShare() throws Exception
    {
        sharer.share(cursor);

        Mockito.verify(intent).setType("text/plain");
        Mockito.verify(intent).putExtra(Mockito.eq(Intent.EXTRA_SUBJECT), Mockito.anyString());
        Mockito.verify(intent).putExtra(Mockito.eq(Intent.EXTRA_TEXT),    Mockito.anyString());
        Mockito.verify(activity).startActivity(intent);
    }
}
