package com.AflamTvMaroc.moviesdarija.ui;

import static android.util.Log.VERBOSE;

import static com.AflamTvMaroc.moviesdarija.util.constants.AppConstants.HUMAN;
import static com.AflamTvMaroc.moviesdarija.util.constants.AppConstants.HUMAN_CLICKS;
import static com.AflamTvMaroc.moviesdarija.util.constants.AppConstants.PROVEN;
import static com.AflamTvMaroc.moviesdarija.util.constants.AppConstants.TIME_OF_APP;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.initializing.ApdInitializationCallback;
import com.appodeal.ads.initializing.ApdInitializationError;
import com.facebook.ads.AdSettings;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.greedygame.core.adview.general.GGAdview;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
import com.AflamTvMaroc.moviesdarija.NotificationExtensionKt;
import com.AflamTvMaroc.moviesdarija.R;
import com.AflamTvMaroc.moviesdarija.adapter.ContentAdapter;
import com.AflamTvMaroc.moviesdarija.api.ApiInterface;
import com.AflamTvMaroc.moviesdarija.api.notification_alarm.NotificationAlarm;
import com.AflamTvMaroc.moviesdarija.interfaces.OnAdStatusChangeListener;
import com.AflamTvMaroc.moviesdarija.interfaces.OnContentClickListener;
import com.AflamTvMaroc.moviesdarija.model.AdsConfig;
import com.AflamTvMaroc.moviesdarija.model.Category;
import com.AflamTvMaroc.moviesdarija.model.Content;
import com.AflamTvMaroc.moviesdarija.util.IntentKeys;
import com.AflamTvMaroc.moviesdarija.util.constants.AppConstants;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseActivity implements OnContentClickListener, OnAdStatusChangeListener {


    boolean doubleBackToExitPressedOnce = false;

    private static final String TAG = "MainActivity";

    private DrawerLayout drawer_layout;
    private RecyclerView main_content_recyclerview;
    private Content clickedContent;
    private ContentAdapter contentAdapter;
    private WorkManager workManager;
    private Long delay;
    private int reminder = 13;
    SharedPreferences prefs;
    LinearLayout bannerContainer;
    LinearLayout layout;


    private String bannerAd;
    private String interstitialAd;
    private String detailAds;

    private Retrofit retrofit;
    boolean isHuman, proven;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotificationExtensionKt.createNotificationChannel(this);
        NotificationAlarm notificationAlarm = new NotificationAlarm();
        notificationAlarm.setAlarm(this);
        bannerContainer = findViewById(R.id.banner_container);

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        isHuman = prefs.getBoolean(HUMAN, true);
        SharedPreferences.Editor editor = prefs.edit();

        AppLovinSdk.initializeSdk(getApplicationContext());
        AdSettings.setDataProcessingOptions( new String[] {} );

        init();
        initInterstitial();


    }

    public void checkInternetSetting() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setTitle(getString(R.string.internet_problem_title));
            alertDialog.setMessage(getString(R.string.internet_problem_text));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.retry), (dialogInterface, i) -> {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkInternetSetting();
                    }
                }, 1000);
                alertDialog.dismiss();
            });
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.black));
        } else {
            getCategories();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
            initInterstitial();


    }
    private void initInterstitial(){
        interstitialAd = getIntent().getStringExtra(IntentKeys.INTENT_ADS_INTERSTITIAL);
        detailAds = getIntent().getStringExtra(IntentKeys.INTENT_ADS_DETAIL);


        proven = prefs.getBoolean(PROVEN,false);
        isHuman = prefs.getBoolean(HUMAN,true);

        if (proven && !isHuman){
            Log.d(TAG, "init: no ads will be shown to a bot");
        }else
        if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_ADMOB)) {
            loadAdmobInterstitialAd();
        }
        else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_FACEBOOK)) {
            loadFacebookInterstitialAd();
        }
        else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN)) {
            loadApplovinInterstitialAd();
        }
        else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN_MEDIATION)) {
            loadApplovinMediationInterstitialAd();
        }
        else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_ADX)) {
            loadAdxInterstitial();
        }
        else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_NONE)) {
            Log.i("ads", "none details");
        }
    }

    //please note, option "off" was not added to the if statements below because it will be redundant
    private void init() {
        setOnAdStatusChangeListener(this);

        bannerAd = getIntent().getStringExtra(IntentKeys.INTENT_ADS_BANNER);
        //bannerAd = getIntent().getStringExtra(IntentKeys.INTENT_ADS_BANNER);

        proven = prefs.getBoolean(PROVEN,false);
        isHuman = prefs.getBoolean(HUMAN,true);
        if (proven && !isHuman){
            Log.d(TAG, "init: no ads will be shown to a bot");
        }
        else if (bannerAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_ADMOB)) {
            loadAdmobBannerAd();
        }
        else if (bannerAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_FACEBOOK)) {
            loadFacebookBannerAd();
        }
        else if (bannerAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN)) {
            loadApplovinBannerAd();
        }
        else if (bannerAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN_MEDIATION)) {
            loadApplovinMediationBannerAd();
        }
        else if (bannerAd.equalsIgnoreCase(AppConstants.ADS_NONE)) {
            Log.i("ads", "none details");
        }
        else if (bannerAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_ADX)) {
            loadAdxBanner(bannerContainer);
        }


        boolean fromVideoError = getIntent().getBooleanExtra(IntentKeys.INTENT_VIDEO_ERROR, false);
        View main_content_wrapper = findViewById(R.id.main_content_wrapper);
        ImageView main_splash_image = findViewById(R.id.main_splash_image);
        drawer_layout = findViewById(R.id.drawer_layout);
        if (drawer_layout != null) {
            drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                    addClicks();
                }

                @Override
                public void onDrawerOpened(@NonNull View drawerView) {
                    addClicks();
                }

                @Override
                public void onDrawerClosed(@NonNull View drawerView) {
                    addClicks();
                }

                @Override
                public void onDrawerStateChanged(int newState) {
                    addClicks();
                }
            });
        }
        if (fromVideoError) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setTitle(getString(R.string.video_error_title));
            alertDialog.setMessage(getString(R.string.video_error_text));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialogInterface, i) -> alertDialog.dismiss());
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.black));
            main_splash_image.setVisibility(View.GONE);
            main_content_wrapper.setVisibility(View.VISIBLE);
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        }
        else {
            main_splash_image.setVisibility(View.VISIBLE);
            main_content_wrapper.setVisibility(View.GONE);
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }


        Toolbar main_toolbar = findViewById(R.id.main_toolbar);
        main_toolbar.setTitle(getString(R.string.app_name));
        main_toolbar.setTitleTextColor(Color.WHITE);
        main_toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(main_toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer_layout, main_toolbar, R.string.open, R.string.close);
        drawer_layout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        main_content_recyclerview = findViewById(R.id.main_content_recyclerview);
        main_content_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        OkHttpClient.Builder client = new OkHttpClient().newBuilder();
        client.addInterceptor(new LoggingInterceptor.Builder()
                .setLevel(Level.BASIC)
                .log(VERBOSE)
                .build());
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            main_splash_image.setVisibility(View.GONE);
            main_content_wrapper.setVisibility(View.VISIBLE);
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        }, 2000);

        TextView main_content_facebook_url = findViewById(R.id.main_content_facebook_url);

        main_content_facebook_url.setOnClickListener(view -> openExternalLink(getString(R.string.facebook_url)));
        TextView main_content_info_link = findViewById(R.id.main_content_info_link);
        main_content_info_link.setOnClickListener(view -> {
//            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
//            if (adsConfig != null) {
//                intent.putExtra(IntentKeys.INTENT_ADS_BANNER, adsConfig.getBanner());
//            }
//            startActivity(intent);
            openExternalLink(getString(R.string.privacy_url));
        });

        checkInternetSetting();

    }


    private void getCategories() {
        ApiInterface service = retrofit.create(ApiInterface.class);

        Call<List<Category>> repos = service.getCategories(ApiInterface.API_CATEGORIES);
        repos.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {

                Log.i("Response", response.body().toString());
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.i("onSuccess", response.body().toString());
                        getContentPerCategory(response.body().get(0).getCategory_url().get(0));
                        populateNavigationDrawer(response.body());

                    } else {
                        Log.i("onEmptyResponse", "Returned empty response");//Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
    }

    private void populateNavigationDrawer(List<Category> categories) {
        NavigationView main_side_nav_view = findViewById(R.id.main_side_nav_view);

        final Menu menu = main_side_nav_view.getMenu();
        menu.clear();
        int id = 0;
        for (Category category : categories) {
            MenuItem menuItem = menu.add(id, id, 0, category.getCategoryname());
            if (category.getExternal_url() != null && !category.getExternal_url().isEmpty() && !category.getExternal_url().get(0).isEmpty()) {
                menuItem.setIcon(R.drawable.ic_link);
            } else if (category.getCategory_url() != null && !category.getCategory_url().isEmpty() && !category.getCategory_url().get(0).isEmpty()) {
                menuItem.setIcon(R.drawable.ic_menu);
            }

            id++;
        }

        main_side_nav_view.setNavigationItemSelectedListener(menuItem ->
        {
            Log.i("Response", "setNavigationItemSelectedListener");
            Category clickedCategory = categories.get(menuItem.getItemId());
            if (clickedCategory.getExternal_url() != null && !clickedCategory.getExternal_url().isEmpty() && !clickedCategory.getExternal_url().get(0).isEmpty()) {
                openExternalLink(clickedCategory.getExternal_url().get(0));
                addClicks();
                drawer_layout.closeDrawer(GravityCompat.START);
            } else if (clickedCategory.getCategory_url() != null && !clickedCategory.getCategory_url().isEmpty() && !clickedCategory.getCategory_url().get(0).isEmpty()) {
                getContentPerCategory(clickedCategory.getCategory_url().get(0));
                addClicks();
                drawer_layout.closeDrawer(GravityCompat.START);
            }

            return false;
        });
    }


    private void getContentPerCategory(String url) {
        ApiInterface service = retrofit.create(ApiInterface.class);

        Call<List<Content>> repos = service.getContentPerCategory(url);
        repos.enqueue(new Callback<List<Content>>() {
            @Override
            public void onResponse(Call<List<Content>> call, Response<List<Content>> response) {

                Log.i("Response", response.body().toString());
                //Toast.makeText()
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        contentAdapter = new ContentAdapter(MainActivity.this, response.body(), MainActivity.this::onItemClick);
                        main_content_recyclerview.setAdapter(contentAdapter);

                    } else {
                        Log.i("onEmptyResponse", "Returned empty response");//Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Content>> call, Throwable t) {

            }

        });
    }

    @Override
    public void onItemClick(Content item) {

        loading();

        addClicks();
        Log.d(TAG, "onItemClick:  check clicks " + getClicks());
        clickedContent = item;

        proven = prefs.getBoolean(PROVEN, false);
        isHuman = prefs.getBoolean(HUMAN, true);

        if (proven && !isHuman) {
            hideLoadingSpinner();
            navigateToDetailsActivity();
        }
        else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_FACEBOOK)) {
            if (!showFacebookInterstitialAd()) {
                showLoadingSpinner(interstitialAd);
            }
        }
        else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_ADMOB)) {
            if (!showAdmobInterstitialAd()) {
                showLoadingSpinner(interstitialAd);
            }
        }
        else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN)) {
            if (!showAppLovinInterstitialAd()) {
                showLoadingSpinner(interstitialAd);
            }
        }
        else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN_MEDIATION)) {
            if (!showAppLovinMediationInterstitialAd()) {
                showLoadingSpinner(interstitialAd);
            }
        }
        else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_ADX)) {
            if (!showAdxInterstitialAd()) {
                showLoadingSpinner(interstitialAd);
            }
        }
        else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_NONE)) {
            hideLoadingSpinner();
            navigateToDetailsActivity();
        }
        else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_OFF)){
            hideLoadingSpinner();
            navigateToDetailsActivity();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (contentAdapter != null)
                    contentAdapter.getFilter().filter(newText);
                return true;
            }
        });


        SearchView finalSearchView = searchView;
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                addClicks();
                finalSearchView.setIconifiedByDefault(true);
                finalSearchView.setFocusable(true);
                finalSearchView.setIconified(false);
                finalSearchView.requestFocusFromTouch();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onAdLoaded() {
        Log.d(TAG, "onAdLoaded: already loaded");
    }

    @Override
    public void onAdFailed(String error) {
        Log.d(TAG, "onAdFailed: reason "+error);
    }


    @Override
    public void onAdClosed() {
        Log.d(TAG, "onAdClosed: it was closed");
        navigateToDetailsActivity();
        addClicks();
    }

    @Override
    public void onAdClicked() {
      addClicks();
    }

    @Override
    public void onHideBanner() {
        hideBanner();
    }

    private void navigateToDetailsActivity() {
        stopLoading = true;
        hideLoadingSpinner();
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(IntentKeys.INTENT_CONTENT, clickedContent);
        intent.putExtra(IntentKeys.INTENT_ADS_BANNER, bannerAd);
        intent.putExtra(IntentKeys.INTENT_ADS_INTERSTITIAL, detailAds);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }
        addClicks();
        Log.d(TAG, "onBackPressed: getting clicks "+getClicks());
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
