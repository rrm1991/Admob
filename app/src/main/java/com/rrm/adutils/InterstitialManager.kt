import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.*

class InterstitialManager
{

    companion object
    {
        private var instance : InterstitialManager? = null

        fun getInstance() : InterstitialManager
        {
            if(instance == null)
                instance = InterstitialManager()

            return instance!!
        }
    }

    private var interstitial: InterstitialAd? = null
    private var interstitialId = "ca-app-pub-3940256099942544/1033173712" //AdMob testing interstitial
    private var adLoadTime = 0L

    fun setInterstitialId(id : String)
    {
        interstitialId = id
    }

    fun loadAd(context: Context, adListener: AdListener) {

        //Ad already loaded, notify activity
        if (isInterstitialReady())
        {
            Log.i("InterstitialManager", "Ad Already Loaded")
            adListener.onAdLoaded()
            return
        }

        //Interstitial creation and ad loading
        val extras = Bundle()
        extras.putString("max_ad_content_rating", "PG")
        val adRequestBuilder: AdRequest.Builder =
            AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
        InterstitialAd.load(
            context,
            interstitialId,
            adRequestBuilder.build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    interstitial = interstitialAd
                    adLoadTime = System.currentTimeMillis()
                    adListener.onAdLoaded()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    adListener.onAdFailedToLoad(loadAdError)
                }
            })
    }

    fun getAd(): InterstitialAd? {
        return interstitial
    }

    fun showAd(activity: Activity?): Boolean {
        if (isInterstitialReady()) {
            interstitial?.setImmersiveMode(true)
            interstitial?.show(activity!!)
            adLoadTime = 0
            return true
        }
        return false
    }

    /*
        Ad is ready if it was loaded less than 4 hours ago and it hasn't been shown yet
     */
    private fun isInterstitialReady(): Boolean {
        return interstitial != null && System.currentTimeMillis() - adLoadTime < 4 * 60 * 60 * 1000
    }

    fun getTestDeviceIds(): List<String> {
        val testDevices = ArrayList<String>()
        testDevices.add("5EAF28079191FBE4C63BEABFE625737D")
        testDevices.add("CD749F8CC1CD16365FD206D703391F1C")
        testDevices.add("A505B4D83FD648EB1F3B3DEDF149A082")
        testDevices.add("6B0F04A6D6E27402E3D0606F3B6B4D1E")
        testDevices.add("C5B02D630C4DF85E20E2E88D6F3C4EB3")
        return testDevices
    }

}