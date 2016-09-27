package net.x4a42.volksempfaenger.data.entity.enclosure;

import net.x4a42.volksempfaenger.data.entity.DaoWrapperBase;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;

import java.util.List;

public class EnclosureDaoWrapper extends DaoWrapperBase<Enclosure>
{
    private final EnclosureProvider provider;

    public EnclosureDaoWrapper(EnclosureDao enclosureDao,
                               EnclosureProvider enclosureProvider)
    {
        super(enclosureDao);
        this.provider = enclosureProvider;
    }

    public Enclosure newEnclosure(Episode episode, String url)
    {
        Enclosure enclosure = provider.get();
        enclosure.setEpisode(episode);
        enclosure.setUrl(url);
        return enclosure;
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
