package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.Activity;
import android.text.Spanned;
import android.widget.TextView;

import net.x4a42.volksempfaenger.HtmlConverter;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.EpisodeCursor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ViewEpisodePresenterTest
{
    Activity             activity       = Mockito.mock(Activity.class);
    HtmlConverter        converter      = Mockito.mock(HtmlConverter.class);
    EpisodeCursor        cursor         = Mockito.mock(EpisodeCursor.class);
    TextView             title          = Mockito.mock(TextView.class);
    TextView             meta           = Mockito.mock(TextView.class);
    TextView             description    = Mockito.mock(TextView.class);
    String               titleStr       = "this-is-my-title";
    String               descriptionStr = "this-is-my-description";
    Spanned              spanned        = Mockito.mock(Spanned.class);
    ViewEpisodePresenter presenter;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(activity.findViewById(R.id.episode_title)).thenReturn(title);
        Mockito.when(activity.findViewById(R.id.episode_meta)).thenReturn(meta);
        Mockito.when(activity.findViewById(R.id.episode_description)).thenReturn(description);

        Mockito.when(cursor.getTitle()).thenReturn(titleStr);
        Mockito.when(cursor.getDescription()).thenReturn(descriptionStr);

        Mockito.when(converter.toSpanned(descriptionStr)).thenReturn(spanned);

        presenter = new ViewEpisodePresenter(activity, converter);
    }

    @Test
    public void onCreate() throws Exception
    {
        presenter.onCreate();

        Mockito.verify(activity).setContentView(R.layout.view_episode);
        Mockito.verify(activity).findViewById(R.id.episode_title);
        Mockito.verify(activity).findViewById(R.id.episode_meta);
        Mockito.verify(activity).findViewById(R.id.episode_description);
    }

    @Test
    public void update()
    {
        presenter.onCreate();
        presenter.update(cursor);

        Mockito.verify(activity).setTitle(titleStr);
        Mockito.verify(title).setText(titleStr);
        Mockito.verify(meta).setText(Mockito.anyString());
        Mockito.verify(description).setText(spanned);
    }
}