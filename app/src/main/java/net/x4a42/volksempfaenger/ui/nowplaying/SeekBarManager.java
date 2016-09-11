package net.x4a42.volksempfaenger.ui.nowplaying;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.RepeatingIntervalTimer;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;

class SeekBarManager implements SeekBar.OnSeekBarChangeListener,
                                Runnable
{
    private final RepeatingIntervalTimer           repeatingIntervalTimer;
    private final PlaybackServiceIntentProvider    intentProvider;
    private PlaybackServiceFacadeProvider          facadeProvider;
    private LinearLayout                           seekBarLayout;
    private SeekBar                                seekBar;
    private TextView                               positionText;
    private TextView                               durationText;

    public SeekBarManager(RepeatingIntervalTimer           repeatingIntervalTimer,
                          PlaybackServiceIntentProvider    intentProvider)
    {
        this.repeatingIntervalTimer = repeatingIntervalTimer;
        this.intentProvider         = intentProvider;
    }

    public SeekBarManager setFacadeProvider(PlaybackServiceFacadeProvider facadeProvider)
    {
        this.facadeProvider = facadeProvider;
        return this;
    }

    public void onCreateView(View view)
    {
        seekBarLayout  = (LinearLayout) view.findViewById(R.id.progress_display);
        seekBar        = (SeekBar)      view.findViewById(R.id.seekbar);
        positionText   = (TextView)     view.findViewById(R.id.position);
        durationText   = (TextView)     view.findViewById(R.id.duration);
        seekBar.setOnSeekBarChangeListener(this);
    }

    public void hide()
    {
        seekBarLayout.setVisibility(View.GONE);
    }

    public void show()
    {
        seekBarLayout.setVisibility(View.VISIBLE);
    }

    public void onResume()
    {
        repeatingIntervalTimer.start();
    }

    public void onPause()
    {
        repeatingIntervalTimer.stop();
    }

    //
    // SeekBar.OnSeekBarChangeListener
    //

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (!fromUser)
        {
            return;
        }

        seekBar.getContext().startService(intentProvider.getSeekIntent(progress));
        repeatingIntervalTimer.start();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        repeatingIntervalTimer.stop();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        // no-op
    }

    //
    // Runnable (for callbacks from RepeatingIntervalTimer)
    //

    @Override
    public void run()
    {
        seekBar.setMax(getDuration());
        seekBar.setProgress(getPosition());
        positionText.setText(Utils.formatTime(getPosition()));
        durationText.setText(Utils.formatTime(getDuration()));
    }

    //
    // helper methods
    //

    private int getPosition()
    {
        PlaybackServiceFacade facade = facadeProvider.getFacade();
        return facade == null ? 0 : facade.getPosition();
    }

    private int getDuration()
    {
        PlaybackServiceFacade facade = facadeProvider.getFacade();
        return facade == null ? 0 : facade.getDuration();
    }
}
