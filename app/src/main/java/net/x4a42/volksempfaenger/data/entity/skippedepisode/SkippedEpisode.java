package net.x4a42.volksempfaenger.data.entity.skippedepisode;

import net.x4a42.volksempfaenger.data.entity.episode.Episode;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import net.x4a42.volksempfaenger.data.entity.enclosure.DaoSession;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDao;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class SkippedEpisode
{
    @Id
    private Long _id;

    @Index
    private long episodeId;

    @ToOne(joinProperty = "episodeId")
    private Episode episode;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 568354709)
    private transient SkippedEpisodeDao myDao;

    @Generated(hash = 1753875889)
    public SkippedEpisode(Long _id, long episodeId) {
        this._id = _id;
        this.episodeId = episodeId;
    }

    @Generated(hash = 1377629155)
    public SkippedEpisode() {
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
    @Generated(hash = 1102841175)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSkippedEpisodeDao() : null;
    }
}
