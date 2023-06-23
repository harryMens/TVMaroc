package com.AflamTvMaroc.moviesdarija.model;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class Category {

    private String categoryname;
    private int active;
    private List<String> category_url;
    private List<String> external_url;

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public List<String> getCategory_url() {
        return category_url;
    }

    public void setCategory_url(List<String> category_url) {
        this.category_url = category_url;
    }

    public List<String> getExternal_url() {
        return external_url;
    }

    public void setExternal_url(List<String> external_url) {
        this.external_url = external_url;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryname='" + categoryname + '\'' +
                ", active=" + active +
                ", category_url=" + category_url +
                ", external_url=" + external_url +
                '}';
    }
}
