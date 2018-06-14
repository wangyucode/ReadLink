package cn.wycode.aidu

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_web_link.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_link)
        progressHandler = ProgressHandle(progress_bar)
        btn_go.setOnClickListener { loadPage() }
        web_view.settings.javaScriptEnabled = true
        web_view.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progress_bar.visibility = View.VISIBLE
                progress_bar.progress = 0
                progressHandler.sendEmptyMessage(0)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progress_bar.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        progressHandler.removeMessages(0)
    }

    private fun loadPage() {
        var address = et_web_address.text.toString()
        if (address.isNotBlank()) {
            if (!address.matches("https://|http://".toRegex())) {
                address = "http://$address"
            }
            web_view.loadUrl(address)
        }
    }
}
