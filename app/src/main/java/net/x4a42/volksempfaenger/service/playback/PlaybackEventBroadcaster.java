package net.x4a42.volksempfaenger.service.playback;

import android.content.Context;
import android.content.Intent;

public class PlaybackEventBroadcaster
{
    private final Context                   context;
    private final PlaybackEventActionMapper playbackEventActionMapper;

    public PlaybackEventBroadcaster(Context context, PlaybackEventActionMapper playbackEventActionMapper)
    {
        this.context                   = context;
        this.playbackEventActionMapper = playbackEventActionMapper;
    }

    public void broadcast(PlaybackEvent playbackEvent)
    {
        Intent intent = new Intent(playbackEventActionMapper.getAction(playbackEvent));
        context.sendBroadcast(intent);
    }
}
