package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.Activity;
import android.content.Intent;

import net.x4a42.volksempfaenger.HtmlConverter;
import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EpisodeSharerTest
{
    @Mock Activity      activity;
    @Mock IntentBuilder intentBuilder;
    @Mock Intent        intent;
    @Mock HtmlConverter converter;
    @Mock Episode       episode;
    @Mock Podcast       podcast;
    EpisodeSharer       sharer;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(intentBuilder.build(Intent.ACTION_SEND)).thenReturn(intent);
        Mockito.when(episode.getPodcast()).thenReturn(podcast);

        sharer = new EpisodeSharer(activity, episode, intentBuilder, converter);
    }

    @Test
    public void testShare() throws Exception
    {
        sharer.share();

        Mockito.verify(intent).setType("text/plain");
        Mockito.verify(intent).putExtra(Mockito.eq(Intent.EXTRA_SUBJECT), Mockito.anyString());
        Mockito.verify(intent).putExtra(Mockito.eq(Intent.EXTRA_TEXT),    Mockito.anyString());
        Mockito.verify(activity).startActivity(intent);
    }
}
