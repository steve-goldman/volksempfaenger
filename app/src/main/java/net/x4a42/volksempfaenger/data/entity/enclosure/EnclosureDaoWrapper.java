package net.x4a42.volksempfaenger.data.entity.enclosure;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;

import java.util.List;

public class EnclosureDaoWrapper
{
    private final EnclosureDao      dao;
    private final EnclosureProvider provider;

    public EnclosureDaoWrapper(EnclosureDao      dao,
                               EnclosureProvider enclosureProvider)
    {
        this.dao      = dao;
        this.provider = enclosureProvider;
    }

    public Enclosure insert(Episode episode, String url, String mimeType, long size)
    {
        Enclosure enclosure = provider.get();
        enclosure.setEpisode(episode);
        enclosure.setUrl(url);
        enclosure.setUrl(url);
        enclosure.setMimeType(mimeType);
        enclosure.setSize(size);
        dao.insert(enclosure);
        return enclosure;
    }

    public void update(Enclosure enclosure, String mimeType, long size)
    {
        enclosure.setMimeType(mimeType);
        enclosure.setSize(size);
        dao.update(enclosure);
    }

    public Enclosure getByUrl(String url)
    {
        List<Enclosure> list = dao.queryBuilder()
                .where(EnclosureDao.Properties.Url.eq(url))
                .list();

        if (list.isEmpty())
        {
            return null;
        }

        return list.get(0);
    }
}
