package com.jayden.batteryalarm.templates;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.jayden.batteryalarm.R;

public class Admobe_Banner_controller {
    public static Admobe_Banner_controller admobe_banner_controler;
    private AdRequest adRequest;
    AdmobeAdsListner admobeAdsListner;
    public AdView mAdView;

    private AdSize getAdSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, ((int)((float)displayMetrics.widthPixels / displayMetrics.density)));
    }

    public static Admobe_Banner_controller getInstance() {
        if (admobe_banner_controler == null) {
            admobe_banner_controler = new Admobe_Banner_controller();
        }
        return admobe_banner_controler;
    }

    public void add_init(Activity activity) {
        if (this.mAdView != null) {
            this.mAdView = null;
        }
        this.mAdView = new AdView((Context)activity);
        this.mAdView.setAdSize(this.getAdSize(activity));
        this.mAdView.setAdUnitId(activity.getResources().getString(R.string.banner_ad_id));
        this.adRequest = new AdRequest.Builder().build();
        this.mAdView.loadAd(this.adRequest);
        this.mAdView.setAdListener(new AdListener(){

            public void onAdFailedToLoad(int n) {
                Constant constant = Constant.getInstance();
                constant.adcountfailbanner = 1 + constant.adcountfailbanner;
                if (Constant.getInstance().adcountfailbanner < 3) {
                    Admobe_Banner_controller.this.adRequest = new AdRequest.Builder().build();
                    Admobe_Banner_controller.this.mAdView.loadAd(Admobe_Banner_controller.this.adRequest);
                    return;
                }
                if (Admobe_Banner_controller.this.admobeAdsListner != null) {
                    Admobe_Banner_controller.this.admobeAdsListner.onAdFailedToLoad(n);
                }
            }

            public void onAdLoaded() {
                Constant.getInstance().adcountfailbanner = 0;
                Constant.getInstance().ismopubshow = true;
                if (Admobe_Banner_controller.this.admobeAdsListner != null) {
                    Admobe_Banner_controller.this.admobeAdsListner.onAdLoaded();
                }
            }
        });
    }

    public boolean checkAddviewNull() {
        return this.mAdView == null;
    }

    public void destroyed_Banner() {
        AdView adView = this.mAdView;
        if (adView != null) {
            adView.destroy();
            this.mAdView = null;
            Constant.getInstance().ismopubshow = false;
        }
    }

    public void initaliza_add(Context context) {
        MobileAds.initialize(context, new OnInitializationCompleteListener(){

            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
    }

    public boolean isAdsshowingornot() {
        return this.mAdView != null;
    }

    public void pause_ads() {
        AdView adView = this.mAdView;
        if (adView != null) {
            adView.pause();
        }
    }

    public void resume_ads() {
        AdView adView = this.mAdView;
        if (adView != null) {
            adView.resume();
        }
    }

    public void setOnAdsShowingListner(AdmobeAdsListner admobeAdsListner) {
        this.admobeAdsListner = admobeAdsListner;
    }

    public static interface AdmobeAdsListner {
        public void onAdFailedToLoad(int var1);

        public void onAdLoaded();
    }


}
