package kr.mbch.www

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface

class WebAppInterface(private val mContext: Context) {

    @JavascriptInterface
    fun vodPlay(vodUrl: String?) {
        mContext.startActivity(
            Intent(
                mContext.applicationContext,
                PlayActivity::class.java
            ).putExtra("vodUrl", vodUrl)
        )
    }

    @JavascriptInterface
    fun vodOther(vodUrl: String?) {
        val intent = Intent("android.intent.action.VIEW", Uri.parse(vodUrl))
        intent.addCategory("android.intent.category.BROWSABLE")
        intent.putExtra(
            "com.android.browser.application_id",
            mContext.packageName
        )
        try {
            mContext.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
        }
    }

//            @JavascriptInterface
//            fun requestDeviceID() {
//                this@MainActivity.regID = GCMRegistrar.getRegistrationId(this@MainActivity)
//                this@MainActivity.webView.loadUrl(
//                    "javascript:deviceRegister('android', '" + this@MainActivity.regID.toString() + "', '" + this@MainActivity.GetDevicesUUID(
//                        this@MainActivity
//                    ).toString() + "');"
//                )
//            }

}