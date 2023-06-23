package com.AflamTvMaroc.moviesdarija.api;

import com.AflamTvMaroc.moviesdarija.model.AdsConfig;
import com.AflamTvMaroc.moviesdarija.model.Category;
import com.AflamTvMaroc.moviesdarija.model.Content;
import com.AflamTvMaroc.moviesdarija.model.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiInterface {

    String BASE_URL = "http://http://ganjaapps.altervista.org/";
    String API_ADS_CONFIG = "http://ganjaapps.altervista.org/Work2022/AflamMarocMenteng/settings.json";
    String API_CATEGORIES = "http://ganjaapps.altervista.org/Work2022/AflamMarocMenteng/categories.json";
    String API_NOTIFICATION = "http://ganjaapps.altervista.org/Work2022/AflamMarocMenteng/push.json";


    @GET
    Call<List<Category>> getCategories(@Url String url);

    @GET
    Call<List<Content>> getContentPerCategory(@Url String url);

    @GET
    Call<List<AdsConfig>> getAdsConfig(@Url String url);


    /**
     * Url to get Notification
     *
     */
    @GET
    Call<List<Notification>> getNotification(@Url String url);
}
