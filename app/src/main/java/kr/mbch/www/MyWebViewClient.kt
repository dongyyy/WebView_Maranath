package kr.mbch.www

import android.webkit.WebView
import android.webkit.WebViewClient

class MyWebViewClientImpl : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return false
    }
}