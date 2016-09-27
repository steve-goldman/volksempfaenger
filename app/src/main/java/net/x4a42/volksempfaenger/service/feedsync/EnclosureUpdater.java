package net.x4a42.volksempfaenger.service.feedsync;

import net.x4a42.volksempfaenger.data.entity.enclosure.Enclosure;
import net.x4a42.volksempfaenger.data.entity.enclosure.EnclosureDaoWrapper;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;

class EnclosureUpdater
{
    private final EnclosureDaoWrapper enclosureDao;

    public EnclosureUpdater(EnclosureDaoWrapper enclosureDao)
    {
        this.enclosureDao = enclosureDao;
    }

    public void insertOrUpdate(Episode episode, net.x4a42.volksempfaenger.feedparser.Enclosure feedEnclosure)
    {
        Enclosure enclosure = enclosureDao.getByUrl(feedEnclosure.url);
        if (enclosure == null)
        {
            insertEnclosure(episode, feedEnclosure);
        }
        else
        {
            updateEnclosure(enclosure, feedEnclosure);
        }
    }

    private void insertEnclosure(Episode episode, net.x4a42.volksempfaenger.feedparser.Enclosure feedEnclosure)
    {
        Enclosure enclosure = enclosureDao.newEnclosure(episode, feedEnclosure.url);

        updateCommonFields(enclosure, feedEnclosure);

        enclosureDao.insert(enclosure);
    }

    private void updateEnclosure(Enclosure enclosure, net.x4a42.volksempfaenger.feedparser.Enclosure feedEnclosure)
    {
        updateCommonFields(enclosure, feedEnclosure);

        enclosureDao.update(enclosure);
    }

    private void updateCommonFields(Enclosure enclosure, net.x4a42.volksempfaenger.feedparser.Enclosure feedEnclosure)
    {
        enclosure.setMimeType(feedEnclosure.mime);
        enclosure.setSize(feedEnclosure.size);
    }
}
