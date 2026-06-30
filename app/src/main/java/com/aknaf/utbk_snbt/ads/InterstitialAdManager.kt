package com.aknaf.utbk_snbt.ads

import android.app.Activity
import android.content.Context
import com.aknaf.utbk_snbt.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object InterstitialAdManager {

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    fun load(context: Context) {
        if (isLoading || interstitialAd != null) return

        isLoading = true

        val adRequest = AdRequest.Builder().build()
        val adUnitId = context.getString(R.string.interstitial_ad_unit_id)

        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                }
            }
        )
    }

    fun show(
        activity: Activity,
        onAdClosed: () -> Unit = {}
    ) {
        val ad = interstitialAd

        if (ad == null) {
            load(activity)
            onAdClosed()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                load(activity)
                onAdClosed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
                load(activity)
                onAdClosed()
            }
        }

        ad.show(activity)
    }
    @JvmStatic
    fun loadAd(context: Context) {
        load(context)
    }

    @JvmStatic
    fun showAd(activity: Activity, onAdClosed: Runnable) {
        show(activity) {
            onAdClosed.run()
        }
    }
}