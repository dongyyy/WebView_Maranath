package kr.mbch.www

import android.app.DownloadManager
import android.content.*
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.view.KeyEvent
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


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
                }
            }

            addJavascriptInterface(WebAppInterface(this@MainActivity), "webViewCall")

            //mp3 다운로드 구현
            setDownloadListener { param1String1: String, param1String2: String, param1String3: String, param1String4: String, param1Long: Long ->
                val intent = Intent("android.intent.action.VIEW")
                intent.setDataAndType(Uri.parse(param1String1), param1String4)

                try{
                    var varParam1String1 = param1String1
                    var varParam1String2 = param1String2
                    var varParam1String3 = param1String3
                    val mimeTypeMap = MimeTypeMap.getSingleton()
                    val downloadManager = this@MainActivity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val uri = Uri.parse(param1String1)
                    varParam1String2 = uri.lastPathSegment.toString()
                    var i = varParam1String3.toLowerCase().lastIndexOf("filename=")
                    
                    if ( i >= 0 ){
                        varParam1String3 = varParam1String3.substring(i + 9);
                        i = varParam1String3.lastIndexOf(";");
                        varParam1String2 = varParam1String3;
                        if (i > 0)
                            varParam1String2 = varParam1String3.substring(0, i - 1);
                    }

                    varParam1String2 = String(Base64.decode(varParam1String2, 0))
                    varParam1String3 = mimeTypeMap.getMimeTypeFromExtension(
                        varParam1String2.substring(
                            varParam1String2.lastIndexOf(".") + 1, varParam1String2.length
                        ).toLowerCase()
                    )!!

                    val request = DownloadManager.Request(uri)
                    request.setTitle(varParam1String2)
                    request.setDescription(varParam1String1)
                    request.setMimeType(varParam1String3)
                    request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        varParam1String2
                    )
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .mkdirs()
                    downloadManager.enqueue(request)
                } catch (e : ActivityNotFoundException){
                    this@MainActivity.startActivity(intent)
                }
            }

            webViewClient = MyWebViewClientImpl(this@MainActivity)

            setInitialScale(1) //이거 넣고 webView 가 폰 화면에 맞음

            loadUrl("http://" + getString(R.string.site_url))
        }

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

            if(bottomMenu.visibility == GONE)
                bottomMenu.visibility = VISIBLE

        }else if (keyCode == KeyEvent.KEYCODE_BACK){
            alertShow("앱을 종료하시겠습니까?", true)
        }

        return false
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
}