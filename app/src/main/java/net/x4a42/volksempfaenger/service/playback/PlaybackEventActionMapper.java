package net.x4a42.volksempfaenger.service.playback;

import android.content.IntentFilter;

/*
    Utility class for PlaybackEventBroadcaster and PlaybackEventReceiver to serialize and
    deserialize PlaybackEvents to and from broadcasts.
 */

class PlaybackEventActionMapper
{
    private static final String ActionPrefix = PlaybackEventActionMapper.class.getPackage().getName();
    private IntentFilter        intentFilter;

    public String getAction(PlaybackEvent playbackEvent)
    {
        return ActionPrefix + "." + playbackEvent.toString();
    }

    public PlaybackEvent getEvent(String action)
    {
        String[] tokens = action.split("\\.");
        return PlaybackEvent.valueOf(tokens[tokens.length - 1]);
    }

    public IntentFilter getIntentFilter()
    {
        if (intentFilter == null)
        {
            intentFilter = new IntentFilter();
            for (PlaybackEvent playbackEvent : PlaybackEvent.values())
            {
                intentFilter.addAction(getAction(playbackEvent));
            }
        }
        return intentFilter;
    }

    public boolean isValid(String action)
    {
        return getIntentFilter().hasAction(action);
    }
}
