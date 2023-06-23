package com.AflamTvMaroc.moviesdarija.ui;

import static com.AflamTvMaroc.moviesdarija.util.constants.AppConstants.HUMAN;
import static com.AflamTvMaroc.moviesdarija.util.constants.AppConstants.HUMAN_CLICKS;
import static com.AflamTvMaroc.moviesdarija.util.constants.AppConstants.PROVEN;
import static com.AflamTvMaroc.moviesdarija.util.constants.AppConstants.TIME_OF_APP;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.AflamTvMaroc.moviesdarija.ui.dialog.LoadingDialog;
import com.applovin.adview.AppLovinAdView;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.BannerView;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.initializing.ApdInitializationCallback;
import com.appodeal.ads.initializing.ApdInitializationError;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.AflamTvMaroc.moviesdarija.R;
import com.AflamTvMaroc.moviesdarija.interfaces.OnAdStatusChangeListener;
import com.AflamTvMaroc.moviesdarija.util.constants.AppConstants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.greedygame.core.AppConfig;
import com.greedygame.core.GreedyGameAds;
import com.greedygame.core.adview.general.AdLoadCallback;
import com.greedygame.core.adview.general.GGAdview;
import com.greedygame.core.interstitial.general.GGInterstitialAd;
import com.greedygame.core.interstitial.general.GGInterstitialEventsListener;
import com.greedygame.core.models.general.AdErrors;

import java.util.List;


public class BaseActivity extends AppCompatActivity {

    private int clicks = 0;
    AppConfig appConfig;
    private static final String TAG = "BaseActivity";
    private com.facebook.ads.AdView facebookAdView;
    private InterstitialAd admobInterstitialAd;
    private MaxInterstitialAd applovinInterstitialAd;
    private MaxInterstitialAd applovinInterstitialAd2;
    private com.facebook.ads.InterstitialAd facebookInterstitialAd;
    private OnAdStatusChangeListener onAdStatusChangeListener;
    private View progress_bar;
    GGInterstitialAd adxAd;
    int counter;
    int counter1;
    boolean stopLoading = false;
    boolean adFailed = false;


    LinearLayout banner_container;
    AdView mAdView;
    MaxAdView applovinAdView;
    MaxAdView applovinAdView2;
    boolean proven, isHuman;
    LoadingDialog loadingDialog;
    private AppLovinAdView bannerAdView;


    public void checkInternetSetting() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setTitle(getString(R.string.internet_problem_title));
            alertDialog.setMessage(getString(R.string.internet_problem_text));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.connect_now), (dialogInterface, i) -> {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                alertDialog.dismiss();
            });

            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialogInterface, i) -> {
                alertDialog.dismiss();
                finishAffinity();
            });
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.black));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.black));
        }
    }
    public void hideBanner(){
        mAdView = findViewById(R.id.adView);
        applovinAdView = findViewById( R.id.applovinAdView );
        applovinAdView2 = findViewById( R.id.applovinAdView2 );
        banner_container =  findViewById(R.id.banner_container);

        banner_container.removeAllViews();
        banner_container.setVisibility(View.GONE);
        mAdView.setVisibility(View.GONE);
        applovinAdView.setVisibility(View.GONE);
        applovinAdView2.setVisibility(View.GONE);
    }


    public void setOnAdStatusChangeListener(OnAdStatusChangeListener onAdStatusChangeListener) {
        this.onAdStatusChangeListener = onAdStatusChangeListener;

        mAdView = findViewById(R.id.adView);
        applovinAdView = findViewById( R.id.applovinAdView );
        applovinAdView2 = findViewById( R.id.applovinAdView2 );
        banner_container =  findViewById(R.id.banner_container);
        loadingDialog = new LoadingDialog(this);
        bannerAdView = new AppLovinAdView(AppLovinAdSize.BANNER, this);






    }

    public void openExternalLink(String externalUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(externalUrl));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.sorry_the_content_cannot_be_loaded), Toast.LENGTH_LONG).show();
        }
    }

    public void showLoadingSpinner(String mainInsterstitial) {
        counter = 0;
        progress_bar = findViewById(R.id.progress_bar);
        adFailed = false;
       // progress_bar.setVisibility(View.VISIBLE);
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //try 8 times every 0.5 seconds

                if (counter >= 17  || adFailed) {
                    stopLoading = true;
                    hideLoadingSpinner();
                    onAdStatusChangeListener.onAdClosed();
                    Toast.makeText(BaseActivity.this, "Ads failed to load", Toast.LENGTH_SHORT).show();
                    return;
                }

                //    just for testing
//                if (counter[0] == 2) {
//                    loadFacebookInterstitialAd();
//                }

                if (mainInsterstitial.equalsIgnoreCase(AppConstants.ADS_SOURCE_FACEBOOK)) {
                    if (!showFacebookInterstitialAd()) {
                        handler.postDelayed(this, 500);
                        counter++;
                    }else {
                        stopLoading = true;
                    }
                } else if (mainInsterstitial.equalsIgnoreCase(AppConstants.ADS_SOURCE_ADMOB)) {
                    if (!showAdmobInterstitialAd()) {
                        handler.postDelayed(this, 500);
                        counter++;
                    }
                    else {
                        stopLoading = true;
                    }
                }
                else if (mainInsterstitial.equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN)) {
                    if (!showAppLovinInterstitialAd()) {
                        handler.postDelayed(this, 500);
                        counter++;
                    }
                    else {
                        stopLoading = true;
                    }
                }else if (mainInsterstitial.equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN_MEDIATION)) {
                    if (!showAppLovinMediationInterstitialAd()) {
                        handler.postDelayed(this, 500);
                        counter++;
                    }
                    else {
                        stopLoading = true;
                    }
                }
                else if (mainInsterstitial.equalsIgnoreCase(AppConstants.ADS_SOURCE_ADX)) {
                    if (!showAdxInterstitialAd()) {
                        handler.postDelayed(this, 500);
                        counter++;
                    }
                    else {
                        stopLoading = true;
                    }
                }
                else {
                    Log.d(TAG, "run: not yet");
                }
                Log.d(TAG, "Counter " + counter);


            }
        }, 900);
    }


    public void loading() {

        counter1 = 0;
        loadingDialog.show();
        loadingDialog.setMax(100);
        loadingDialog.setProgress(0);
        stopLoading = false;

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!stopLoading) {
                    if (counter1 < 100) {
                        handler.postDelayed(this, 500);
                        counter1 += 4;
                        loadingDialog.setProgress(counter1);
                    }
                    else {
                        hideLoadingSpinner();
                    }
                }
                else{
                    hideLoadingSpinner();
                }
            }
        }, 500);
    }


    public void hideLoadingSpinner() {
        stopLoading = true;
        loadingDialog.setProgress(100);
        loadingDialog.dismiss();
       // progress_bar.setVisibility(View.GONE);
    }

    //---- facebook
    public void loadFacebookBannerAd() {
        Log.d(TAG, "loadFacebookBannerAd");
        AdSettings.setTestMode(true);
        facebookAdView = new com.facebook.ads.AdView(this, getString(R.string.facebook_banner_id), AdSize.BANNER_HEIGHT_50);
        banner_container.setVisibility(View.VISIBLE);
        banner_container.removeAllViews();
        banner_container.addView(facebookAdView);
        facebookAdView.loadAd();

        com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d(TAG, "loadFacebookBannerAd " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d("", "onAdLoaded " + ad);
                onAdStatusChangeListener.onAdLoaded();
            }

            @Override
            public void onAdClicked(Ad ad) {
                onAdStatusChangeListener.onAdClicked();
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        com.facebook.ads.AdView.AdViewLoadConfig loadAdConfig = facebookAdView.buildLoadAdConfig().withAdListener(adListener).build();
        facebookAdView.loadAd(loadAdConfig);
    }

    public void loadFacebookInterstitialAd() {
        Log.d(TAG, "loadFacebookInterstitialAd");
        AdSettings.setTestMode(true);
        facebookInterstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.facebook_interstial_id));
        com.facebook.ads.InterstitialAdListener adListener = new com.facebook.ads.InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                onAdStatusChangeListener.onAdClosed();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                onAdStatusChangeListener.onAdFailed(adError.getErrorMessage());
                Log.d(TAG, "loadFacebookInterstitialAd " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                onAdStatusChangeListener.onAdLoaded();
            }

            @Override
            public void onAdClicked(Ad ad) {
                onAdStatusChangeListener.onAdClicked();
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = facebookInterstitialAd.buildLoadAdConfig().withAdListener(adListener).build();
        if (facebookInterstitialAd != null && !facebookInterstitialAd.isAdLoaded()) {
            facebookInterstitialAd.loadAd(loadAdConfig);
        }
    }


    public boolean showFacebookInterstitialAd() {
        if (facebookInterstitialAd != null && facebookInterstitialAd.isAdLoaded()) {
            facebookInterstitialAd.show();
            hideLoadingSpinner();
            return true;
        }
        return false;
    }

    //---- admob
    public void loadAdmobBannerAd() {

        mAdView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    onAdStatusChangeListener.onAdLoaded();
                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    // Code to be executed when an ad request fails.
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                    onAdStatusChangeListener.onAdClicked();
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }
            });
        mAdView.loadAd(adRequest);
    }

    public void loadAdmobInterstitialAd() {

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,getString(R.string.admob_interestial_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        onAdStatusChangeListener.onAdLoaded();
                        admobInterstitialAd = interstitialAd;
                        admobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                onAdStatusChangeListener.onAdClosed();
                            }
                        });
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        onAdStatusChangeListener.onAdFailed(loadAdError.getMessage());
                        admobInterstitialAd = null;
                    }



                });
    }

    public boolean showAdmobInterstitialAd() {
        if (admobInterstitialAd != null) {
            admobInterstitialAd.show(this);
            hideLoadingSpinner();
            return true;
        }
        return false;
    }

    private void maxSdkIntialize(){
        // Max/Applovin SDK initialize
        AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk(this, new AppLovinSdk.SdkInitializationListener() {
            public void onSdkInitialized(AppLovinSdkConfiguration appLovinSdkConfiguration) {
                //banner ads after intialize
                Log.e(TAG, "onSdkInitialized: banerrrrr" );
                //applovinAdView.startAutoRefresh();
                applovinAdView.setListener(new MaxAdViewAdListener() {
                    @Override
                    public void onAdExpanded(MaxAd ad) {
                        Log.d(TAG, "applovin banner onAdExpanded");

                    }

                    @Override
                    public void onAdCollapsed(MaxAd ad) {
                        Log.d(TAG, "applovin banner onAdCollapsed");

                    }

                    @Override
                    public void onAdLoaded(MaxAd ad) {
                        Log.d(TAG, "applovin banner onAdLoaded");
                        applovinAdView.setVisibility( View.VISIBLE);
                        onAdStatusChangeListener.onAdLoaded();

                    }

                    @Override
                    public void onAdDisplayed(MaxAd ad) {
                        Log.d(TAG, "applovin banner onAdDisplayed");

                    }

                    @Override
                    public void onAdHidden(MaxAd ad) {
                        Log.d(TAG, "applovin banner onAdHidden");
                    }

                    @Override
                    public void onAdClicked(MaxAd ad) {
                        Log.d(TAG, "applovin banner onAdClicked");
                        onAdStatusChangeListener.onAdClicked();

                    }

                    @Override
                    public void onAdLoadFailed(String adUnitId, MaxError error) {
                        applovinAdView.setVisibility( View.GONE);
                        Log.e(TAG, "applovin banner onAdLoadFailed" + error.getMessage());
//                returned no eligible ads from any mediated networks for this app/device.
                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                        Log.d(TAG, "applovin banner onAdDisplayFailed");

                    }
                });
                applovinAdView.loadAd();

            }
        });
    }


    //app lovin
    public void loadApplovinBannerAd() {
    //    AppLovinSdk.getInstance(this).setMediationProvider("max");

        // Load the ad
        maxSdkIntialize();




    }

    //app lovin
    public void loadApplovinMediationBannerAd() {
        // Load the ad
        applovinAdView2.loadAd();
        applovinAdView2.startAutoRefresh();

        applovinAdView2.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {
                Log.d(TAG, "applovin banner onAdExpanded");

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {
                Log.d(TAG, "applovin banner onAdCollapsed");

            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.d(TAG, "applovin banner onAdLoaded");
                applovinAdView2.setVisibility( View.VISIBLE);
                onAdStatusChangeListener.onAdLoaded();

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                Log.d(TAG, "applovin banner onAdDisplayed");

            }

            @Override
            public void onAdHidden(MaxAd ad) {
                Log.d(TAG, "applovin banner onAdHidden");
            }

            @Override
            public void onAdClicked(MaxAd ad) {
                Log.d(TAG, "applovin banner onAdClicked");
                onAdStatusChangeListener.onAdClicked();

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                applovinAdView2.setVisibility( View.GONE);
                Log.e(TAG, "applovin banner onAdLoadFailed" + error.getMessage());
//                returned no eligible ads from any mediated networks for this app/device.
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Log.d(TAG, "applovin banner onAdDisplayFailed");

            }
        });

    }


    public void loadApplovinInterstitialAd() {
        applovinInterstitialAd = new MaxInterstitialAd( getString(R.string.app_lovin_interstitial_unit_id), this );
        applovinInterstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.d(TAG, "applovin onAdLoaded");
                onAdStatusChangeListener.onAdLoaded();

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                Log.d(TAG, "applovin onAdDisplayed");
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                Log.d(TAG, "applovin onAdHidden");
                onAdStatusChangeListener.onAdClosed();

            }

            @Override
            public void onAdClicked(MaxAd ad) {
                Log.d(TAG, "applovin onAdClicked");
                onAdStatusChangeListener.onAdClicked();
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Log.e(TAG, "applovin onAdLoadFailed " + error.getMessage());
                onAdStatusChangeListener.onAdFailed(error.getMessage());

            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Log.e(TAG, "applovin onAdDisplayFailed " + error.getMessage());

            }
        });

        // Load the first ad
        applovinInterstitialAd.loadAd();

    }

    public boolean showAppLovinInterstitialAd() {
        Log.d(TAG, "applovin showAppLovinInterstitialAd");
        if (applovinInterstitialAd != null && applovinInterstitialAd.isReady()) {
            Log.d(TAG, "applovin showAppLovinInterstitialAd entrato nell IF");
            applovinInterstitialAd.showAd();
            hideLoadingSpinner();
            return true;
        }
        return false;
    }


    public void loadApplovinMediationInterstitialAd() {
        applovinInterstitialAd2 = new MaxInterstitialAd( getString(R.string.app_lovin_interstitial_med_unit_id), this );
        applovinInterstitialAd2.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.d(TAG, "applovin onAdLoaded");
                onAdStatusChangeListener.onAdLoaded();

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                Log.d(TAG, "applovin onAdDisplayed");
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                Log.d(TAG, "applovin onAdHidden");
                onAdStatusChangeListener.onAdClosed();

            }

            @Override
            public void onAdClicked(MaxAd ad) {
                Log.d(TAG, "applovin onAdClicked");
                onAdStatusChangeListener.onAdClicked();
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Log.e(TAG, "applovin onAdLoadFailed " + error.getMessage());
                onAdStatusChangeListener.onAdFailed(error.getMessage());

            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Log.e(TAG, "applovin onAdDisplayFailed " + error.getMessage());

            }
        });

        // Load the first ad
        applovinInterstitialAd2.loadAd();

    }

    public boolean showAppLovinMediationInterstitialAd() {
        Log.d(TAG, "applovin showAppLovinInterstitialAd");
        if (applovinInterstitialAd2 != null && applovinInterstitialAd2.isReady()) {
            Log.d(TAG, "applovin showAppLovinInterstitialAd entrato nell IF");
            applovinInterstitialAd2.showAd();
            hideLoadingSpinner();

            return true;
        }
        return false;
    }

    //adx
    public void loadAdxSdk(){
         appConfig = new AppConfig.Builder(this)
                .withAppId(getString(R.string.adx_app_id))  //Replace the app ID with your app's ID
                .build();
        //In case you want callback for initialization,
        //you can provide an greedyGameAdsEventsListener
        //for the same or assign *null* value
        GreedyGameAds.initWith(appConfig,null);


    }

    public void loadAdxInterstitial(){
        loadAdxSdk();
        String name = getString(R.string.adx_interstitial_id);
        adxAd = new GGInterstitialAd(this,name);


        adxAd.setListener(new GGInterstitialEventsListener() {
            @Override
            public void onAdLoaded() {
               // adxAd.show();
                onAdStatusChangeListener.onAdLoaded();
                Log.d(TAG, "onAdLoaded: inter");
            }

            @Override
            public void onAdLoadFailed(@NonNull AdErrors adErrors) {
                onAdStatusChangeListener.onAdFailed(adErrors.toString());
                Log.d(TAG, "onAdLoadFailed: error in loading "+adErrors.toString());
            }

            @Override
            public void onAdShowFailed() {

            }

            @Override
            public void onAdOpened() {
                onAdStatusChangeListener.onAdClicked();
                Log.d(TAG, "onAdOpened: adx ads clicked");
            }

            @Override
            public void onAdClosed() {
                onAdStatusChangeListener.onAdClosed();
                adxAd.destroy();
            }
        });

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean isHuman = prefs.getBoolean(HUMAN, true);
        if (isHuman) {
            adxAd.loadAd();
        }


    }
    public boolean showAdxInterstitialAd() {
        if (adxAd != null && adxAd.isAdLoaded()) {
            adxAd.show(this);
            return true;
        }
        return false;
    }

    public void loadAdxBanner(LinearLayout layout){
        loadAdxSdk();

        GGAdview ggAdView1 = new GGAdview(this);
        ggAdView1.setUnitId(getString(R.string.adx_banner_id));  //Replace with your Ad Unit ID here
        ggAdView1.setAdsMaxHeight(50); //Value is in pixels, not in dp

        LinearLayout.LayoutParams layoutParams = new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50);
        layout.removeAllViews();
        layout.addView(ggAdView1,layoutParams);
        layout.setVisibility(View.VISIBLE);

        ggAdView1.loadAd(new AdLoadCallback() {
            @Override
            public void onAdLoaded() {
                onAdStatusChangeListener.onAdLoaded();
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                boolean isHuman = prefs.getBoolean(HUMAN, true);
            }

            @Override
            public void onAdLoadFailed(@NonNull AdErrors adErrors) {
                Log.d(TAG, "onAdLoaded: sdfglsddfgdlfgdfgdfg");


            }

            @Override
            public void onUiiOpened() {
                onAdStatusChangeListener.onAdClicked();
                Log.d(TAG, "onUiiOpened: adx banner ads clicked");
            }

            @Override
            public void onUiiClosed() {
                Log.d(TAG, "onAdLoaded: closed");

            }

            @Override
            public void onReadyForRefresh() {
                Log.d(TAG, "refresh: sdfglsddfgdlfgdfgdfg");
                appConfig.destroy();

            }
        });

    }

    @Override
    protected void onDestroy() {
        if (facebookAdView != null) {
            facebookAdView.destroy();
        }
        super.onDestroy();
    }


    public void addClicks() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        isHuman = prefs.getBoolean(HUMAN, true);
        proven = prefs.getBoolean(PROVEN, false);

        if (!proven) {
            SharedPreferences.Editor editor = prefs.edit();
            clicks = prefs.getInt(HUMAN_CLICKS, 0);
            if (clicks == 0) {
                float floating = SystemClock.elapsedRealtime();
                editor.putInt(HUMAN_CLICKS, 1);
                editor.putFloat(TIME_OF_APP, floating);
            } else {
                float firstClick1 = prefs.getFloat(TIME_OF_APP, 0.0f);
                Log.d(TAG, "addClicks: medoba "+firstClick1);
                clicks++;
                editor.remove(HUMAN_CLICKS);
                editor.putInt(HUMAN_CLICKS, clicks);

                if (clicks > 4) {
                    float firstClick = prefs.getFloat(TIME_OF_APP, 0.0f);
                    double currentClick = SystemClock.elapsedRealtime();
                    double timeElapsed = currentClick - firstClick;
                    if (timeElapsed < 0){
                        timeElapsed *= -1;
                    }
                    double  average = timeElapsed/clicks;
                    Log.d(TAG, "addClicks: average clicks " + average);
                    Log.d(TAG, "addClicks: current clicks " + currentClick);
                    Log.d(TAG, "addClicks: first clicks " + firstClick);
                    Log.d(TAG, "addClicks: e=[lapsed clicks " + timeElapsed);


                    if (average < 600) {
                        editor.putBoolean(HUMAN, false);
                        editor.putBoolean(PROVEN,true);
                        onAdStatusChangeListener.onHideBanner();
                    }
                    else{
                        editor.putBoolean(PROVEN,true);
                    }
                }
            }
            editor.apply();
            proven = prefs.getBoolean(PROVEN,false);
            isHuman = prefs.getBoolean(HUMAN,true);

        }
    }
    public Integer getClicks(){
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        return prefs.getInt(HUMAN_CLICKS,0);
    }

    public Boolean isDialogShowing(){
        return loadingDialog.isShowing();
    }

}
