package net.x4a42.volksempfaenger.data.entity.enclosure;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDao;

@Entity
public class Enclosure
{
    @Id
    private Long _id;

    @Index
    private long episodeId;

    @ToOne(joinProperty = "episodeId")
    private Episode episode;

    @Unique
    private String url;

    private String mimeType;

    private long size;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1421012836)
    private transient EnclosureDao myDao;

    @Generated(hash = 1697180468)
    public Enclosure(Long _id, long episodeId, String url, String mimeType,
            long size) {
        this._id = _id;
        this.episodeId = episodeId;
        this.url = url;
        this.mimeType = mimeType;
        this.size = size;
    }

    @Generated(hash = 1998344103)
    public Enclosure() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public long getEpisodeId() {
        return this.episodeId;
    }

    public void setEpisodeId(long episodeId) {
        this.episodeId = episodeId;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Generated(hash = 1965513091)
    private transient Long episode__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1596751597)
    public Episode getEpisode() {
        long __key = this.episodeId;
        if (episode__resolvedKey == null || !episode__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            EpisodeDao targetDao = daoSession.getEpisodeDao();
            Episode episodeNew = targetDao.load(__key);
            synchronized (this) {
                episode = episodeNew;
                episode__resolvedKey = __key;
            }
        }
        return episode;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1071193317)
    public void setEpisode(@NotNull Episode episode) {
        if (episode == null) {
            throw new DaoException(
                    "To-one property 'episodeId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.episode = episode;
            episodeId = episode.get_id();
            episode__resolvedKey = episodeId;
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 871728734)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getEnclosureDao() : null;
    }
}
