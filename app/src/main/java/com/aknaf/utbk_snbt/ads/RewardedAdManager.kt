package com.aknaf.utbk_snbt.ads

import android.app.Activity
import android.content.Context
import com.aknaf.utbk_snbt.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object RewardedAdManager {

    private var rewardedAd: RewardedAd? = null
    private var isLoading = false

    fun load(context: Context) {
        if (isLoading || rewardedAd != null) return

        isLoading = true

        val adRequest = AdRequest.Builder().build()
        val adUnitId = context.getString(R.string.rewarded_ad_unit_id)

        RewardedAd.load(
            context,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isLoading = false
                }
            }
        )
    }

    fun show(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onAdClosed: () -> Unit = {}
    ) {
        val ad = rewardedAd

        if (ad == null) {
            load(activity)
            onAdClosed()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                load(activity)
                onAdClosed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                rewardedAd = null
                load(activity)
                onAdClosed()
            }
        }

        ad.show(activity) {
            onRewardEarned()
        }
    }
}