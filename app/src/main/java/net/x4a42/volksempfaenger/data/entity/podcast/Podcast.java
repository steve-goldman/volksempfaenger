package net.x4a42.volksempfaenger.data.entity.podcast;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.List;
import org.greenrobot.greendao.DaoException;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoSession;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDao;

@Entity
public class Podcast
{
    @Id
    private Long _id;

    @NotNull
    @Unique
    private String feedUrl;

    @ToMany(referencedJoinProperty = "podcastId")
    private List<Episode> episodes;

    private String title;

    private String description;

    private String website;

    private Long lastUpdate;

    private Long httpExpires;

    private Long httpLastModified;

    private Long httpEtag;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1372568415)
    private transient PodcastDao myDao;

    @Generated(hash = 445536486)
    public Podcast(Long _id, @NotNull String feedUrl, String title,
            String description, String website, Long lastUpdate, Long httpExpires,
            Long httpLastModified, Long httpEtag) {
        this._id = _id;
        this.feedUrl = feedUrl;
        this.title = title;
        this.description = description;
        this.website = website;
        this.lastUpdate = lastUpdate;
        this.httpExpires = httpExpires;
        this.httpLastModified = httpLastModified;
        this.httpEtag = httpEtag;
    }

    @Generated(hash = 1435461188)
    public Podcast() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getFeedUrl() {
        return this.feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
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

    public String getWebsite() {
        return this.website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Long getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getHttpExpires() {
        return this.httpExpires;
    }

    public void setHttpExpires(Long httpExpires) {
        this.httpExpires = httpExpires;
    }

    public Long getHttpLastModified() {
        return this.httpLastModified;
    }

    public void setHttpLastModified(Long httpLastModified) {
        this.httpLastModified = httpLastModified;
    }

    public Long getHttpEtag() {
        return this.httpEtag;
    }

    public void setHttpEtag(Long httpEtag) {
        this.httpEtag = httpEtag;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 15381845)
    public List<Episode> getEpisodes() {
        if (episodes == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            EpisodeDao targetDao = daoSession.getEpisodeDao();
            List<Episode> episodesNew = targetDao._queryPodcast_Episodes(_id);
            synchronized (this) {
                if(episodes == null) {
                    episodes = episodesNew;
                }
            }
        }
        return episodes;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1306836270)
    public synchronized void resetEpisodes() {
        episodes = null;
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 655073716)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPodcastDao() : null;
    }
}
