package com.AflamTvMaroc.moviesdarija.model;

import androidx.annotation.Keep;

@Keep
public class AdsConfig {
    private String banner;
    private String MainInsterstitial;
    private String DetailInterstitial;

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getMainInsterstitial() {
        return MainInsterstitial;
    }

    public void setMainInsterstitial(String mainInsterstitial) {
        MainInsterstitial = mainInsterstitial;
    }

    public String getDetailInterstitial() {
        return DetailInterstitial;
    }

    public void setDetailInterstitial(String detailInterstitial) {
        DetailInterstitial = detailInterstitial;
    }
}
