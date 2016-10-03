package net.x4a42.volksempfaenger.service.playlistdownload;

import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownload;
import net.x4a42.volksempfaenger.data.entity.episodedownload.EpisodeDownloadDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItem;
import net.x4a42.volksempfaenger.data.entity.playlistitem.PlaylistItemDaoWrapper;

import java.util.List;

class PlaylistDownloadTaskProxy
{
    private final Preferences                   preferences;
    private final PlaylistItemDaoWrapper        playlistItemDao;
    private final EpisodeDownloadDaoWrapper     episodeDownloadDao;

    public PlaylistDownloadTaskProxy(Preferences preferences,
                                     PlaylistItemDaoWrapper playlistItemDao,
                                     EpisodeDownloadDaoWrapper episodeDownloadDao)
    {
        this.preferences        = preferences;
        this.playlistItemDao    = playlistItemDao;
        this.episodeDownloadDao = episodeDownloadDao;
    }

    public void doInBackground()
    {
        addTopN();
        removeRest();
    }

    public void onPostExecute()
    {
        // TODO: start the download service
    }

    private void addTopN()
    {
        List<PlaylistItem> playlistItems
                = playlistItemDao.getFirstN(preferences.getDownloadedQueueCount());

        for (PlaylistItem playlistItem : playlistItems)
        {
            Episode episode = playlistItem.getEpisode();
            if (!episodeDownloadDao.hasEpisode(episode))
            {
                episodeDownloadDao.insert(episode);
            }
        }
    }

    private void removeRest()
    {
        List<EpisodeDownload> episodeDownloads = episodeDownloadDao.getAll();

        for (EpisodeDownload episodeDownload : episodeDownloads)
        {
            Episode episode = episodeDownload.getEpisode();
            if (playlistItemDao.hasEpisode(episode))
            {
                PlaylistItem playlistItem = playlistItemDao.getByEpisode(episode);
                if (playlistItem.getPosition() > preferences.getDownloadedQueueCount())
                {
                    remove(episodeDownload);
                }
            }
            else
            {
                remove(episodeDownload);
            }
        }
    }

    private void remove(EpisodeDownload episodeDownload)
    {
        episodeDownloadDao.delete(episodeDownload);
    }
}
