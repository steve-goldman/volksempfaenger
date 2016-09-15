package net.x4a42.volksempfaenger.data.entity.episode;

import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.enclosure.Enclosure;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoSession;
import net.x4a42.volksempfaenger.data.entity.enclosure.EnclosureDao;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastDao;

@Entity
public class Episode
{
    @Id
    private Long _id;

    @Index
    private long podcastId;

    @ToOne(joinProperty = "podcastId")
    private Podcast podcast;

    @ToMany(referencedJoinProperty = "episodeId")
    private List<Enclosure> enclosures;

    @Unique
    private String episodeUrl;

    private String title;

    private String description;

    private Long pubDate;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 494421999)
    private transient EpisodeDao myDao;

    @Generated(hash = 611928901)
    public Episode(Long _id, long podcastId, String episodeUrl, String title,
            String description, Long pubDate) {
        this._id = _id;
        this.podcastId = podcastId;
        this.episodeUrl = episodeUrl;
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
    }

    @Generated(hash = 1336866052)
    public Episode() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public long getPodcastId() {
        return this.podcastId;
    }

    public void setPodcastId(long podcastId) {
        this.podcastId = podcastId;
    }

    public String getEpisodeUrl() {
        return this.episodeUrl;
    }

    public void setEpisodeUrl(String episodeUrl) {
        this.episodeUrl = episodeUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPubDate() {
        return this.pubDate;
    }

    public void setPubDate(Long pubDate) {
        this.pubDate = pubDate;
    }

    @Generated(hash = 86411217)
    private transient Long podcast__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 690400662)
    public Podcast getPodcast() {
        long __key = this.podcastId;
        if (podcast__resolvedKey == null || !podcast__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PodcastDao targetDao = daoSession.getPodcastDao();
            Podcast podcastNew = targetDao.load(__key);
            synchronized (this) {
                podcast = podcastNew;
                podcast__resolvedKey = __key;
            }
        }
        return podcast;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 255235274)
    public void setPodcast(@NotNull Podcast podcast) {
        if (podcast == null) {
            throw new DaoException(
                    "To-one property 'podcastId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.podcast = podcast;
            podcastId = podcast.get_id();
            podcast__resolvedKey = podcastId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1033180182)
    public List<Enclosure> getEnclosures() {
        if (enclosures == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            EnclosureDao targetDao = daoSession.getEnclosureDao();
            List<Enclosure> enclosuresNew = targetDao._queryEpisode_Enclosures(_id);
            synchronized (this) {
                if(enclosures == null) {
                    enclosures = enclosuresNew;
                }
            }
        }
        return enclosures;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1844152894)
    public synchronized void resetEnclosures() {
        enclosures = null;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 153634245)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getEpisodeDao() : null;
    }
}
