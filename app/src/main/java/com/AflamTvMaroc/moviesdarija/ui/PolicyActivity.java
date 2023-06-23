package com.AflamTvMaroc.moviesdarija.ui;

import static android.util.Log.VERBOSE;

import static com.AflamTvMaroc.moviesdarija.util.constants.AppConstants.HUMAN;
import static com.AflamTvMaroc.moviesdarija.util.constants.AppConstants.TIME_OF_APP;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.AflamTvMaroc.moviesdarija.R;
import com.AflamTvMaroc.moviesdarija.api.ApiInterface;
import com.AflamTvMaroc.moviesdarija.interfaces.OnAdStatusChangeListener;
import com.AflamTvMaroc.moviesdarija.model.AdsConfig;
import com.AflamTvMaroc.moviesdarija.ui.dialog.LoadingDialog;
import com.AflamTvMaroc.moviesdarija.util.IntentKeys;
import com.AflamTvMaroc.moviesdarija.util.constants.AppConstants;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.initializing.ApdInitializationCallback;
import com.appodeal.ads.initializing.ApdInitializationError;
import com.appodeal.ads.regulator.CCPAUserConsent;
import com.appodeal.ads.regulator.GDPRUserConsent;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryPerformance;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PolicyActivity extends BaseActivity implements OnAdStatusChangeListener {
    private static final String TAG = "PolicyActivity";

    boolean doubleBackToExitPressedOnce = false;
    Button btnAccept, btnClose;
    RelativeLayout policyLayout;

    private Retrofit retrofit;
    private AdsConfig adsConfig;
    ProgressDialog progress;
    int progress1 = 0;

    @Override
    public void onAdLoaded() {

    }

    @Override
    public void onAdFailed(String error) {

    }

    @Override
    public void onAdClosed() {
        Intent intent = new Intent(PolicyActivity.this, MainActivity.class);
        intent.putExtra(IntentKeys.INTENT_ADS_BANNER, adsConfig.getBanner());
        intent.putExtra(IntentKeys.INTENT_ADS_INTERSTITIAL, adsConfig.getMainInsterstitial());
        startActivity(intent);
        finish();
    }

    @Override
    public void onAdClicked() {

    }

    @Override
    public void onHideBanner() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        Appodeal.initialize(this, getString(R.string.appodeal_key), Appodeal.INTERSTITIAL | Appodeal.BANNER_BOTTOM, new ApdInitializationCallback() {
            @Override
            public void onInitializationFinished(List<ApdInitializationError> list) {

            }
        });
        Appodeal.updateGDPRUserConsent(GDPRUserConsent.Personalized);
        Appodeal.updateCCPAUserConsent(CCPAUserConsent.OptIn);

        setOnAdStatusChangeListener(this);

        new FlurryAgent.Builder()
                .withDataSaleOptOut(false) //CCPA - the default value is false
                .withCaptureUncaughtExceptions(true)
                .withIncludeBackgroundSessionsInMetrics(true)
                .withLogLevel(VERBOSE)
                .withPerformanceMetrics(FlurryPerformance.ALL)
                .build(this, getString(R.string.flurry_apy_key));

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

        Toolbar main_toolbar = findViewById(R.id.main_toolbar);
        main_toolbar.setTitle(getString(R.string.app_name));
        main_toolbar.setTitleTextColor(Color.WHITE);
        main_toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(main_toolbar);

        btnAccept = findViewById(R.id.policy_accept_button);
        btnClose = findViewById(R.id.policy_close_button);
        policyLayout = findViewById(R.id.policy_layout);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        btnClose.setOnClickListener(v -> finishAffinity());
        btnAccept.setOnClickListener(v -> {

            Intent intent = new Intent(PolicyActivity.this, MainActivity.class);
            intent.putExtra(IntentKeys.INTENT_ADS_BANNER, adsConfig.getBanner());
            intent.putExtra(IntentKeys.INTENT_ADS_INTERSTITIAL, adsConfig.getMainInsterstitial());
            intent.putExtra(IntentKeys.INTENT_ADS_DETAIL, adsConfig.getDetailInterstitial());
            startActivity(intent);
            finish();

//            if (adsConfig.getMainInsterstitial().equalsIgnoreCase(AppConstants.ADS_SOURCE_FACEBOOK)) {
//                if (!showFacebookInterstitialAd()) {
//                    showLoadingSpinner(adsConfig.getMainInsterstitial());
//                }
//            } else if (adsConfig.getMainInsterstitial().equalsIgnoreCase(AppConstants.ADS_SOURCE_ADMOB)) {
//                if (!showAdmobInterstitialAd()) {
//                    showLoadingSpinner(adsConfig.getMainInsterstitial());
//                }
//            } else if (adsConfig.getMainInsterstitial().equalsIgnoreCase(AppConstants.ADS_SOURCE_APPODEAL)) {
//                if (!showAppodealInterstitialAd()) {
//                    showLoadingSpinner(adsConfig.getMainInsterstitial());
//                }
//            } else if (adsConfig.getMainInsterstitial().equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN)) {
//                if (!showAppLovinInterstitialAd()) {
//                    showLoadingSpinner(adsConfig.getMainInsterstitial());
//                }
//            } else if (adsConfig.getMainInsterstitial().equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN_MEDIATION)) {
//                if (!showAppLovinMediationInterstitialAd()) {
//                    showLoadingSpinner(adsConfig.getMainInsterstitial());
//                }
//            } else if (adsConfig.getMainInsterstitial().equalsIgnoreCase(AppConstants.ADS_SOURCE_HUAWEI_IMAGE_ADS) || adsConfig.getMainInsterstitial().equalsIgnoreCase(AppConstants.ADS_SOURCE_HUAWEI_VIDEO_ADS)) {
//                if (!showHuaweiInterstitialAd()) {
//                    showLoadingSpinner(adsConfig.getMainInsterstitial());
//                }
//            }
//            else if (adsConfig.getMainInsterstitial().equalsIgnoreCase(AppConstants.ADS_SOURCE_ADX)) {
//                if (!showAdxInterstitialAd()) {
//                    showLoadingSpinner(adsConfig.getMainInsterstitial());
//                }
//            }else if (adsConfig.getMainInsterstitial().equalsIgnoreCase(AppConstants.ADS_NONE)) {
//                Intent intent = new Intent(PolicyActivity.this, MainActivity.class);
//                intent.putExtra(IntentKeys.INTENT_ADS_BANNER, adsConfig.getBanner());
//                intent.putExtra(IntentKeys.INTENT_ADS_INTERSTITIAL, adsConfig.getMainInsterstitial());
//                startActivity(intent);
//                finish();
//            }
//            else if (adsConfig.getMainInsterstitial().equalsIgnoreCase(AppConstants.ADS_SOURCE_OFF)) {
//                Intent intent = new Intent(PolicyActivity.this, MainActivity.class);
//                intent.putExtra(IntentKeys.INTENT_ADS_BANNER, adsConfig.getBanner());
//                intent.putExtra(IntentKeys.INTENT_ADS_INTERSTITIAL, adsConfig.getMainInsterstitial());
//                startActivity(intent);
//                finish();
//            }

        });

        checkInternetSetting();


    }




    public void checkInternetSetting() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PolicyActivity.this);
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

        }
        else {
            getAdsConfig();
        }
    }


    private void getAdsConfig() {
        showProgressBar();

        ApiInterface service = retrofit.create(ApiInterface.class);

        Call<List<AdsConfig>> repos = service.getAdsConfig(ApiInterface.API_ADS_CONFIG);
        repos.enqueue(new Callback<List<AdsConfig>>() {
            @Override
            public void onResponse(Call<List<AdsConfig>> call, Response<List<AdsConfig>> response) {
                progress.dismiss();

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        adsConfig = response.body().get(0);


                        Log.d(TAG, "onResponse: banner "+adsConfig.getBanner());
                        Log.d(TAG, "onResponse: main "+adsConfig.getMainInsterstitial());
                        Log.d(TAG, "onResponse: detail "+adsConfig.getDetailInterstitial());
                    }
                    else {

                        Log.i("onEmptyResponse", "Returned empty response");
                        adsConfig = new AdsConfig();
                        adsConfig.setBanner(AppConstants.ADS_NONE);
                        adsConfig.setMainInsterstitial(AppConstants.ADS_NONE);
                        adsConfig.setDetailInterstitial(AppConstants.ADS_NONE);
                    }
                }
                else {
                    adsConfig = new AdsConfig();
                    adsConfig.setBanner(AppConstants.ADS_NONE);
                    adsConfig.setMainInsterstitial(AppConstants.ADS_NONE);
                    adsConfig.setDetailInterstitial(AppConstants.ADS_NONE);
                }
            }

            @Override
            public void onFailure(Call<List<AdsConfig>> call, Throwable t) {
                progress.dismiss();
                adsConfig = new AdsConfig();
                adsConfig.setBanner(AppConstants.ADS_NONE);
                adsConfig.setMainInsterstitial(AppConstants.ADS_NONE);
                adsConfig.setDetailInterstitial(AppConstants.ADS_NONE);
            }
        });
    }

    private void showProgressBar() {
        progress = new ProgressDialog(this);
        progress.setMessage("Loading");
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
        progress.show();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                progress.dismiss();
//            }
//        }, Integer.parseInt(getResources().getString(R.string.time_loading_ads)));

    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }

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