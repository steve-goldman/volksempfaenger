package net.x4a42.volksempfaenger.ui.main;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.event.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventListener;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.service.syncall.SyncAllServiceIntentProvider;
import net.x4a42.volksempfaenger.ui.addsubscription.AddSubscriptionActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.settings.SettingsActivityIntentProvider;

class OptionsMenuManager implements PlaybackServiceConnectionManager.Listener,
                                    PlaybackEventListener
{
    private final Context                               context;
    private final AddSubscriptionActivityIntentProvider addSubscriptionIntentProvider;
    private final SyncAllServiceIntentProvider          syncAllIntentProvider;
    private final PlaybackServiceIntentProvider         playbackIntentProvider;
    private final SettingsActivityIntentProvider        settingsIntentProvider;
    private final PlaybackServiceFacadeProvider         facadeProvider;
    private boolean                                     created;
    private MenuItem                                    play;
    private MenuItem                                    pause;

    public OptionsMenuManager(Context                               context,
                              AddSubscriptionActivityIntentProvider addSubscriptionIntentProvider,
                              SyncAllServiceIntentProvider          syncAllIntentProvider,
                              PlaybackServiceIntentProvider         playbackIntentProvider,
                              SettingsActivityIntentProvider        settingsIntentProvider,
                              PlaybackServiceFacadeProvider         facadeProvider)
    {
        this.context                       = context;
        this.addSubscriptionIntentProvider = addSubscriptionIntentProvider;
        this.syncAllIntentProvider         = syncAllIntentProvider;
        this.playbackIntentProvider        = playbackIntentProvider;
        this.settingsIntentProvider        = settingsIntentProvider;
        this.facadeProvider                = facadeProvider;
    }

    public boolean onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.subscription_list, menu);
        play  = menu.findItem(R.id.item_play);
        pause = menu.findItem(R.id.item_pause);
        created = true;
        updatePlayPause();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.item_play:
            case R.id.item_pause:
                context.startService(playbackIntentProvider.getPlayPauseIntent());
                return true;
            case R.id.item_add:
                context.startActivity(addSubscriptionIntentProvider.get());
                return true;
            case R.id.item_update:
                context.startService(syncAllIntentProvider.getSyncIntent());
                return true;
            case R.id.item_settings:
                context.startActivity(settingsIntentProvider.get());
                return true;
            case android.R.id.home:
                return true;
        }
        return false;
    }

    @Override
    public void onPlaybackServiceConnected()
    {
        if (!created)
        {
            return;
        }

        updatePlayPause();
    }

    @Override
    public void onPlaybackServiceDisconnected()
    {
        setPaused();
    }

    @Override
    public void onPlaybackEvent(PlaybackEvent playbackEvent)
    {
        updatePlayPause();
    }

    private void updatePlayPause()
    {
        PlaybackServiceFacade facade = facadeProvider.getFacade();
        if (facade != null && facade.isPlaying())
        {
            setPlaying();
        }
        else
        {
            setPaused();
        }
    }

    private void setPlaying()
    {
        play.setVisible(false);
        pause.setVisible(true);
    }

    private void setPaused()
    {
        play.setVisible(true);
        pause.setVisible(false);
    }

}
