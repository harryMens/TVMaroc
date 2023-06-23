//package com.AflamTvMaroc.moviesdarija.ui;
//
//import android.graphics.Color;
//import android.os.Bundle;
//
//import androidx.appcompat.widget.Toolbar;
//
//import com.AflamTvMaroc.moviesdarija.R;
//import com.AflamTvMaroc.moviesdarija.util.IntentKeys;
//import com.AflamTvMaroc.moviesdarija.util.constants.AppConstants;
//
//public class InfoActivity extends BaseActivity {
//
//    private static final String TAG = "InfoActivity";
//    private String bannerAd;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_info);
//
//        init();
//    }
//
//    private void init() {
//
//        Toolbar info_toolbar = findViewById(R.id.info_toolbar);
//        info_toolbar.setTitle(getString(R.string.app_name));
//        info_toolbar.setTitleTextColor(Color.WHITE);
//        info_toolbar.setSubtitleTextColor(Color.WHITE);
//
//        bannerAd = getIntent().getStringExtra(IntentKeys.INTENT_ADS_BANNER);
//        if (bannerAd != null) {
//            if (bannerAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_ADMOB)) {
//                loadAdmobBannerAd();
//            } else if (bannerAd.equalsIgnoreCase(AppConstants.ADS_SOURCE_FACEBOOK)) {
//                loadFacebookBannerAd();
//            }
//        }
//
//    }
//
//
//}
