package net.x4a42.volksempfaenger.data.entity.podcast;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Podcast
{
    @Id
    private Long _id;

    @NotNull
    @Unique
    private String feedUrl;

    private String title;

    private String description;

    private String website;

    private Long lastUpdate;

    private Long httpExpires;

    private Long httpLastModified;

    private Long httpEtag;

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
}
