package cn.wycode.aidu

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_text.*
import kotlinx.android.synthetic.main.activity_web_link.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.lang.ref.WeakReference


class WebLinkActivity : AppCompatActivity() {


    class ProgressHandle(progressBar: ProgressBar) : Handler() {
        var progressAddDelay = 100L
        private val _progressBar = WeakReference<ProgressBar>(progressBar)
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            val p = _progressBar.get()
            if (p != null) {
                p.progress++
                progressAddDelay += 10
                sendEmptyMessageDelayed(0, progressAddDelay)
            }

        }
    }

    lateinit var progressHandler: ProgressHandle

    lateinit var mService: PlayService
    var mBound = false

    lateinit var loadingAlert: AlertDialog
    lateinit var javaScriptLocalObj: InJavaScriptLocalObj

    private lateinit var localBroadcastManager: LocalBroadcastManager

    private var playReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getIntExtra(EXTRA_PLAYER_WHAT, 0)) {
                EXTRA_PLAYER_WHAT_SYN_START -> {

                }
                EXTRA_PLAYER_WHAT_SPEECH_START -> {
                    if (loadingAlert.isShowing)
                        loadingAlert.dismiss()
                    view_player_link.setTitleText(intent.getStringExtra(EXTRA_PLAYER_PARAM_1))
                    view_player_link.setContentText(intent.getStringExtra(EXTRA_PLAYER_PARAM_2))
                    view_player_link.isPlaying = true
                }

                EXTRA_PLAYER_WHAT_SPEECH_FINISH -> {
                    view_player_link.setTitleText("停止中")
                    view_player_link.setContentText(getString(R.string.no_content))
                    view_player_link.isPlaying = false
                }
            }
        }
    }


    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as PlayService.PlayServiceBinder
            mService = binder.getService()
            javaScriptLocalObj.mService = mService
            if (mService.isPlaying) {
                view_player_link.setTitleText("播放文字中")
                view_player_link.setContentText(mService.currentReadText)
            }
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_link)
        setupPlayer()
        setupWebView()
    }

    private fun setupPlayer() {
        val intent = Intent(this, PlayService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)

        loadingAlert = AlertDialog.Builder(this)
                .setTitle("处理中...")
                .setView(ProgressBar(this))
                .create()

        view_player_link.setOnPlayOrPauseClickedListener(View.OnClickListener {
            if (mBound) {
                if (view_player.isPlaying) {
                    mService.stop()
                } else {
                    loadPage()
                }
            }
        })

        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(playReceiver, IntentFilter(ACTION_PLAYER_EVENT))
    }

    private fun setupWebView() {
        progressHandler = ProgressHandle(progress_bar)
        btn_go.setOnClickListener { loadPage() }
        web_view.settings.javaScriptEnabled = true
        javaScriptLocalObj = InJavaScriptLocalObj()
        web_view.addJavascriptInterface(javaScriptLocalObj, "java_obj")
        web_view.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progress_bar.visibility = View.VISIBLE
                progress_bar.progress = 0
                progressHandler.sendEmptyMessage(0)
            }

            override fun onPageFinished(view: WebView, url: String?) {
                view.loadUrl("javascript:window.java_obj.getSource(document.getElementsByTagName('body')[0].innerHTML);")
                super.onPageFinished(view, url)
                progress_bar.visibility = View.GONE
                progressHandler.removeMessages(0)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        progressHandler.removeMessages(0)
        unbindService(mConnection)
        localBroadcastManager.unregisterReceiver(playReceiver)
    }

    private fun loadPage() {
        var address = et_web_address.text.toString()
        if (address.isNotBlank()) {
            if (!address.matches("^(https|http)://.*".toRegex())) {
                address = "http://$address"
            }
            web_view.loadUrl(address)
        }
        loadingAlert.show()
    }

    class InJavaScriptLocalObj() {

        lateinit var mService: PlayService

        @JavascriptInterface
        fun getSource(html: String) {
            val doc = Jsoup.parse(html)
            val body = doc.body()
            val stringBuilder = StringBuilder()
            parse(body,stringBuilder)
            mService.speakText(stringBuilder.toString(), "正在朗读文字")
        }

        fun parse(element: Element, stringBuilder: StringBuilder) {
            val tagName = element.tagName().toLowerCase()
            if (tagName == "h1" ||
                    tagName == "h2" ||
                    tagName == "h3" ||
                    tagName == "h4" ||
                    tagName == "h5" ||
                    tagName == "h6" ||
                    tagName == "p"
            ) {
                stringBuilder.append(element.text())

            } else if (element.children().isNotEmpty()) {
                for (e in element.children()) {
                    parse(e, stringBuilder)
                }
            }
        }
    }


}
