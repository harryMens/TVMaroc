package com.AflamTvMaroc.moviesdarija.model;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.List;

@Keep
public class Content implements Serializable {

    private int active;
    private String logo;
    private String info;
    private String title;
    private List<String> stream_url;
    private List<String> external_link;

    public Content(Category clickedCategory) {
        this.title = clickedCategory.getCategoryname();
        this.external_link = clickedCategory.getExternal_url();
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getStream_url() {
        return stream_url;
    }

    public void setStream_url(List<String> stream_url) {
        this.stream_url = stream_url;
    }

    public List<String> getExternal_link() {
        return external_link;
    }

    public void setExternal_link(List<String> external_link) {
        this.external_link = external_link;
    }

    @Override
    public String toString() {
        return "Content{" +
                "active=" + active +
                ", logo='" + logo + '\'' +
                ", info='" + info + '\'' +
                ", title='" + title + '\'' +
                ", stream_url=" + stream_url +
                ", external_link=" + external_link +
                '}';
    }
}
