package com.AflamTvMaroc.moviesdarija.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.appodeal.ads.Appodeal;
import com.AflamTvMaroc.moviesdarija.R;
import com.AflamTvMaroc.moviesdarija.interfaces.OnAdStatusChangeListener;
import com.AflamTvMaroc.moviesdarija.model.Content;
import com.AflamTvMaroc.moviesdarija.ui.dialog.FiveStarsDialog;
import com.AflamTvMaroc.moviesdarija.util.IntentKeys;
import com.AflamTvMaroc.moviesdarija.util.constants.AppConstants;
import com.appodeal.ads.initializing.ApdInitializationCallback;
import com.appodeal.ads.initializing.ApdInitializationError;
import com.appodeal.ads.regulator.CCPAUserConsent;
import com.appodeal.ads.regulator.GDPRUserConsent;
import com.facebook.ads.AdSettings;
import com.greedygame.core.adview.general.GGAdview;
import com.squareup.picasso.Picasso;

import static com.AflamTvMaroc.moviesdarija.ui.dialog.FiveStarsDialog.SP_DISABLED;
import static com.AflamTvMaroc.moviesdarija.util.constants.AppConstants.HUMAN;
import static com.AflamTvMaroc.moviesdarija.util.constants.AppConstants.HUMAN_CLICKS;
import static com.AflamTvMaroc.moviesdarija.util.constants.AppConstants.PROVEN;
import static com.AflamTvMaroc.moviesdarija.util.constants.AppConstants.TIME_OF_APP;

import java.util.List;

public class DetailsActivity extends BaseActivity implements OnAdStatusChangeListener {

    private static final String TAG = "DetailsActivity";
    private Content content;
    private String bannerAd;
    private String interstitialAd;
    private View details_rate_button;
    private boolean isHuman;
    LinearLayout bannerContainer;

    RelativeLayout relativeLayout;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        content = (Content) getIntent().getSerializableExtra(IntentKeys.INTENT_CONTENT);

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        relativeLayout = findViewById(R.id.ad_wrapper);
        bannerContainer = findViewById(R.id.banner_container);

        AdSettings.setDataProcessingOptions( new String[] {} );
        AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
                // AppLovin SDK is initialized, start loading ads
            }
        } );
        init();

    }

    //please note, option "off" was not added to the if statements below because it will be redundant
    private void init() {
        setOnAdStatusChangeListener(this);



        bannerAd = getIntent().getStringExtra(IntentKeys.INTENT_ADS_BANNER);
        interstitialAd = getIntent().getStringExtra(IntentKeys.INTENT_ADS_INTERSTITIAL);

        proven = prefs.getBoolean(PROVEN,false);
        isHuman = prefs.getBoolean(HUMAN,true);

        if (proven && !isHuman){
            Log.d(TAG, "init: no ads to shown to bots");
        }
        else if (bannerAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_ADMOB)) {
            loadAdmobBannerAd();
        } else if (bannerAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_FACEBOOK)) {
            loadFacebookBannerAd();
        } else if (bannerAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN)) {
            loadApplovinBannerAd();
        } else if (bannerAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN_MEDIATION)) {
            loadApplovinMediationBannerAd();
        }  else if (bannerAd.equalsIgnoreCase(AppConstants.ADS_NONE)) {
            Log.i("ads", "none details");
        }
        else if (bannerAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_ADX)) {
            loadAdxBanner(bannerContainer);
        }


        if (proven && !isHuman){
            Log.d(TAG, "init: no ads to be shown to bots");
        }
        else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_ADMOB)) {
            loadAdmobInterstitialAd();
        } else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_FACEBOOK)) {
            loadFacebookInterstitialAd();
        } else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN)) {
            loadApplovinInterstitialAd();
        } else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN_MEDIATION)) {
            loadApplovinMediationInterstitialAd();
        }  else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_NONE)) {
            Log.i("ads", "none details");
        } else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_ADX)) {
            loadAdxInterstitial();
        }


    Toolbar details_toolbar = findViewById(R.id.details_toolbar);
        details_toolbar.setTitle(getString(R.string.app_name));
        details_toolbar.setTitleTextColor(Color.WHITE);
        details_toolbar.setSubtitleTextColor(Color.WHITE);

        ImageView details_image = findViewById(R.id.details_image);
        TextView details_title = findViewById(R.id.details_title);
        TextView details_description = findViewById(R.id.details_description);

        if (content == null){
            content = (Content) getIntent().getSerializableExtra(IntentKeys.INTENT_CONTENT);
        }
        details_title.setText(content.getTitle());
        details_description.setText(content.getInfo());

        if (content.getLogo() != null && content.getLogo().startsWith("http")) {
            Picasso.get().load(Uri.parse(content.getLogo())).into(details_image);
        } else {
            details_image.setImageResource(R.mipmap.ic_launcher);
        }

        Button details_go_button = findViewById(R.id.details_go_button);

        if (content.getExternal_link() != null && !content.getExternal_link().isEmpty() && !content.getExternal_link().get(0).isEmpty()) {
            details_go_button.setText(getString(R.string.open));
        } else if (content.getStream_url() != null && !content.getStream_url().isEmpty() && !content.getStream_url().get(0).isEmpty()) {
            details_go_button.setText(getString(R.string.play));
        }
        details_go_button.setOnClickListener(view -> {
            loading();
                openContent();
            addClicks();
            Log.d(TAG, "onItemClick:  check clicks "+getClicks());

        });
    }


    @Override
    protected void onResume() {

        super.onResume();
        content = (Content) getIntent().getSerializableExtra(IntentKeys.INTENT_CONTENT);
        details_rate_button = findViewById(R.id.details_rate_button);
        boolean disabledRateButton = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE).getBoolean(SP_DISABLED, false);
        if (!disabledRateButton) {
            details_rate_button.setVisibility(View.VISIBLE);
            details_rate_button.setOnClickListener(v -> {
                showRateDialog();
                addClicks();
            });
        } else {
            details_rate_button.setVisibility(View.GONE);
        }

            init();

    }


    private void showRateDialog() {
        FiveStarsDialog fiveStarsDialog = new FiveStarsDialog(this, "dev@ourcodeworld.com");
        fiveStarsDialog.setNegativeButtonText("");
        fiveStarsDialog.setNeutralButton("");
        fiveStarsDialog.setPositiveButtonText(getString(R.string.rate));
        fiveStarsDialog.setRateText(getString(R.string.rate_title))
                .setTitle(getString(R.string.rate_text))
                .setForceMode(false)
                .setUpperBound(4)
                .setNegativeReviewListener(stars -> {

                })
                .setReviewListener(rating -> {
                    details_rate_button.setVisibility(View.GONE);
                    Log.d(TAG, "onReview: " + rating);
                    if (rating >= 4) {
                        openMarket();
                    } else {
                        Toast.makeText(DetailsActivity.this, getString(R.string.thank_you_for_rating), Toast.LENGTH_LONG).show();
                    }
                })
                // should the dialog appear (0 == immediately)
                .showAfter(0);
    }

    private void openMarket() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void openContent() {
        proven = prefs.getBoolean(PROVEN,false);
        isHuman = prefs.getBoolean(HUMAN,true);
        if (proven && !isHuman){
            doFunctionInApp();
        }
        else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_FACEBOOK)) {
            if (!showFacebookInterstitialAd()) {
                showLoadingSpinner(interstitialAd);
            }
        } else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_ADMOB)) {
            if (!showAdmobInterstitialAd()) {
                showLoadingSpinner(interstitialAd);
            }
        } else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN)) {
            if (!showAppLovinInterstitialAd()) {
                showLoadingSpinner(interstitialAd);
            }
        } else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_APPLOVIN_MEDIATION)) {
            if (!showAppLovinMediationInterstitialAd()) {
                showLoadingSpinner(interstitialAd);
            }
        }  else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_NONE)) {
            doFunctionInApp();
        }
        else if (interstitialAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_ADX)) {
            if (!showAdxInterstitialAd()) {
                showLoadingSpinner(interstitialAd);
            }
        }
    }

    @Override
    public void onAdLoaded() {

        Log.d(TAG, "onAdLoaded: already loaded");
    }

    @Override
    public void onAdFailed(String error) {

    }


    @Override
    public void onAdClosed() {
        stopLoading = true;
        hideLoadingSpinner();

        Log.e(TAG, "onAdClosed: called in ad cloosed");
        doFunctionInApp();
        addClicks();
    }

    @Override
    public void onAdClicked() {
    }

    @Override
    public void onHideBanner() {
        hideBanner();
    }

    private void doFunctionInApp() {
        if (content.getExternal_link() != null && !content.getExternal_link().isEmpty() && !content.getExternal_link().get(0).isEmpty()) {
            openExternalLink(content.getExternal_link().get(0));
        } else if (content.getStream_url() != null && !content.getStream_url().isEmpty() && !content.getStream_url().get(0).isEmpty()) {
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra(IntentKeys.INTENT_VIDEO_URL, content.getStream_url().get(0));
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
            addClicks();
    }

}
