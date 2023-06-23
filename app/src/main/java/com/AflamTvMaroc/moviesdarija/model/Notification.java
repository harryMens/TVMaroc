package com.AflamTvMaroc.moviesdarija.model;

import androidx.annotation.Keep;

@Keep
public class Notification {
    private String active;
    private String title;
    private String body;
    private String url;


    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
