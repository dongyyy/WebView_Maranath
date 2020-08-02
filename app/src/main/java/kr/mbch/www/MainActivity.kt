package kr.mbch.www

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


//TODO :

class MainActivity : AppCompatActivity() {
    private lateinit var completeReceiver: BroadcastReceiver

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isNetworkConnected()) {
            alertShow("네트워크에 접속할수 없습니다. 잠시후 다시 시도하세요", false)
        }

        completeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                val resources: Resources = context.resources
                Toast.makeText(context, "파일 다운로드가 완료되었습니다.", Toast.LENGTH_LONG).show()
                this@MainActivity.startActivity(Intent("android.intent.action.VIEW_DOWNLOADS"))
            }
        }


        mainWebView.apply {
            //계정 중복 팝업 안떠서 넣은 3총사
            settings.javaScriptEnabled = true
            settings.setSupportMultipleWindows(true)
            settings.javaScriptCanOpenWindowsAutomatically = true

            settings.userAgentString = StringBuffer(settings.userAgentString).append(";ANYLINEAPP").toString() // 이거 넣고 한동안 안죽음
//            focusable

            //Zoom
            settings.builtInZoomControls = true
            settings.setSupportZoom(true)
            settings.displayZoomControls = false

            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.defaultTextEncodingName = "UTF-8"

//            webChromeClient = MyWebChromeClientImpl(this@MainActivity.applicationContext)
            webChromeClient = object : WebChromeClient(){
                override fun onJsAlert(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    result: JsResult?
                ): Boolean {
                    return super.onJsAlert(view, url, message, result)
//                    AlertDialog.Builder(this@MainActivity)
//                        .setTitle(context.getString(R.string.message))
//                        .setMessage(message)
//                        .setPositiveButton(context.getString(R.string.confirm)){ _,_-> result
//
//                        }
//                        .setCancelable(false)
//                        .create().show()
//                    return true
                }
            }

            addJavascriptInterface(WebAppInterface(this@MainActivity), "webViewCall")

            webViewClient = MyWebViewClientImpl()
            setInitialScale(1) //이거 넣고 webView 가 폰 화면에 맞음
//            loadUrl("http://www.mbch.kr")
            loadUrl("http://" + getString(R.string.site_url))
        }


        /* access modifiers changed from: private */
//        public boolean isOnline() {
//            try {
//                ConnectivityManager conMan = (ConnectivityManager) getSystemService("connectivity");
//                NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState();
//                if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
//                    return true;
//                }
//                NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState();
//                if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
//                    return true;
//                }
//                return false;
//            } catch (NullPointerException e) {
//                return false;
//            }
//        }

        imageView1.setOnClickListener {
            mainWebView.loadUrl("http://" + getString(R.string.site_url))
        }

        imageView2.setOnClickListener{
            mainWebView.loadUrl("http://" + getString(R.string.site_url) + "/core/mobile/board/new.html")
        }

        imageView3.setOnClickListener{
            mainWebView.loadUrl("http://" + getString(R.string.site_url) + "/core/mobile/notification/notification.html")
        }

        imageView4.setOnClickListener{
                //http://www.mbch.kr/main/sub.html?pageCode=10001
            mainWebView.loadUrl("http://" + getString(R.string.site_url)+"/main/sub.html?pageCode=10001")
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }



    fun alertShow(msg: String, isCancelBtn: Boolean) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        alertDialog.setTitle("알림")
        alertDialog.setMessage(msg)
        alertDialog.setPositiveButton(
            "확인"
        ) { dialog, _ ->
            dialog.dismiss()
            finish()
        }
        if (isCancelBtn) {
            alertDialog.setNegativeButton(
                "취소"
            ) { dialog, _ ->
                dialog.cancel() }
        }
        alertDialog.show()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(completeReceiver);
    }

    override fun onResume() {
        registerReceiver(completeReceiver, IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));
        super.onResume()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (this.mainWebView.canGoBack())) {
            val gUrl: String = mainWebView.url
            if (gUrl.indexOf("/main/main.html") > 0) {
                alertShow("앱을 종료하시겠습니까?", true)
            }else{
                mainWebView.goBack()
            }
        }else if (keyCode == KeyEvent.KEYCODE_BACK){
            alertShow("앱을 종료하시겠습니까?", true)
        }

        return false

//        if (keyCode === 4 && this.webView.canGoBack()) {
//            val gUrl: String = this.webView.getUrl()
//            if (gUrl.indexOf("/main/main.html") > 0) {
//                alertShow("앱을 종료하시겠습니까?", true)
//            } else if (gUrl.indexOf("/main/sub.html?pageCode=10001") > 0) {
//                this.webView.loadUrl("http://" + getString(R.string.site_url) + "/main/main.html")
//            } else {
//                val postPageArr =
//                    arrayOf("process.php", "loginCheck.php", "logout.php")
//                var isPost = false
//                val webHistory: WebBackForwardList = this.webView.copyBackForwardList()
//                val historyUrl =
//                    webHistory.getItemAtIndex(webHistory.currentIndex - 1).url
//                var i = 0
//                while (true) {
//                    if (i >= postPageArr.size) {
//                        break
//                    } else if (historyUrl.indexOf(postPageArr[i]) > 0) {
//                        isPost = true
//                        break
//                    } else {
//                        i++
//                    }
//                }
//                if (isPost.toBoolean()) {
//                    this.webView.goBackOrForward(-2)
//                } else {
//                    this.webView.goBack()
//                }
//            }
//        } else if (keyCode === 4) {
//            alertShow("앱을 종료하시겠습니까?", true)
//        }
//        return false


//        return super.onKeyDown(keyCode, event)
    }
}