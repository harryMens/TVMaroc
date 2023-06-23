package com.AflamTvMaroc.moviesdarija.interfaces;

public interface OnAdStatusChangeListener {
    void onAdLoaded();
    void onAdFailed(String errorMessage);
    void onAdClosed();
    void onAdClicked();
    void onHideBanner();
}
