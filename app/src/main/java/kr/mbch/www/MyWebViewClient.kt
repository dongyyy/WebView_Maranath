package kr.mbch.www

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.activity_main.*

class MyWebViewClientImpl(private val mContext: Context) : WebViewClient() {
    val GOOGLE_PLAY_STORE_PREFIX = "market://details?id="
    val INTENT_PROTOCOL_END = ";end;"
    val INTENT_PROTOCOL_INTENT = "#Intent;"
    val INTENT_PROTOCOL_START = "intent:"

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        //https://jizard.tistory.com/149 참
        val url = request?.url.toString()
        if (url.startsWith(INTENT_PROTOCOL_START)) {
            val customUrlStartIndex = INTENT_PROTOCOL_START.length
            val customUrlEndIndex = url.lastIndexOf(INTENT_PROTOCOL_END)
            if (0 > customUrlEndIndex) {
                return false
            } else {
                val customUrl = url.substring(customUrlStartIndex, customUrlEndIndex)
                try {
                    mContext.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)))
                } catch (e: ActivityNotFoundException) {
//                                val packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length
//                                val packageEndIndex = url.indexOf(INTENT_PROTOCOL_END)

//                                val packageName = url.substring(packageStartIndex, if (packageEndIndex < 0 ) url.length else packageEndIndex)
//                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)))
                    if(url.contains("kakao")) {
                        mContext.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(GOOGLE_PLAY_STORE_PREFIX + "com.kakao.talk")
                            )
                        )
                    }

                }
                return true // webview 밖으로 이동
            }
        } else if (url.contains("www.youtube.com")) {
            mContext.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            return true
        } else if (url.contains("player.vimeo.com")) {
            (mContext as MainActivity).bottomMenu.visibility = View.GONE
            return false
        } else {
            return false //webview 내부에서 이동
        }
    }
}