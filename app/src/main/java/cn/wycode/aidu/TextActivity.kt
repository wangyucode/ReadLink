package cn.wycode.aidu

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.TtsMode
import kotlinx.android.synthetic.main.activity_text.*
import android.content.ComponentName
import android.content.Context
import android.os.IBinder
import android.content.ServiceConnection



class TextActivity : AppCompatActivity() {

    val AppId = "11382258"
    val AppKey = "sH8rGkPvBgjVPEsn03mGf3bT"
    val AppSecret = "Ec0aUGa8kgKlnKkclNcBScKSqDuRu2vy"

    var mService: PlayService? = null
    var mBound = false

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as PlayService.PlayServiceBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

        val intent = Intent(this, PlayService::class.java)
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE)

        btn_read.setOnClickListener {
            if(mBound) {
                mService!!.speakText(editText.text.toString())
            }
        }
    }
}
