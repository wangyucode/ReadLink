package cn.wycode.aidu

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.TtsMode

const val ACTION_PLAYER_EVENT = "ACTION_PLAYER_EVENT"
const val EXTRA_PLAYER_WHAT = "EXTRA_PLAYER_WHAT"
const val EXTRA_PLAYER_WHAT_SYN_FINISH = 1

class PlayService : Service(), SpeechSynthesizerListener {


    val AppId = "11382258"
    val AppKey = "sH8rGkPvBgjVPEsn03mGf3bT"
    val AppSecret = "Ec0aUGa8kgKlnKkclNcBScKSqDuRu2vy"

    private var mSpeechSynthesizer: SpeechSynthesizer = SpeechSynthesizer.getInstance()

    var localBroadcastManager = LocalBroadcastManager.getInstance(this)

    init {
        mSpeechSynthesizer.setContext(this)
        mSpeechSynthesizer.setSpeechSynthesizerListener(this) //listener是SpeechSynthesizerListener 的实现类，需要实现您自己的业务逻辑。SDK合成后会对这个类的方法进行回调。
        mSpeechSynthesizer.setAppId(AppId)
        mSpeechSynthesizer.setApiKey(AppKey, AppSecret)
    }

    inner class PlayServiceBinder : Binder() {
        fun getService(): PlayService {
            return this@PlayService
        }
    }

    private val binder = PlayServiceBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }


    override fun onCreate() {
        super.onCreate()
        mSpeechSynthesizer.auth(TtsMode.ONLINE)
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0")
        mSpeechSynthesizer.initTts(TtsMode.ONLINE)
    }

    fun speakText(text: String) {
        mSpeechSynthesizer.speak(text)
    }


    override fun onSynthesizeStart(p0: String?) {
        Log.d("wy", "onSynthesizeStart-->$p0")
    }

    override fun onSpeechFinish(p0: String?) {
        Log.d("wy", "onSpeechFinish-->$p0")
    }

    override fun onSpeechProgressChanged(p0: String?, p1: Int) {
        Log.d("wy", "onSpeechProgressChanged-->$p0,$p1")
    }

    override fun onSynthesizeFinish(p0: String?) {
        Log.d("wy", "onSynthesizeFinish-->$p0")
        var intent = Intent(ACTION_PLAYER_EVENT)
        intent.putExtra(EXTRA_PLAYER_WHAT, EXTRA_PLAYER_WHAT_SYN_FINISH)
        localBroadcastManager.sendBroadcast(intent)
    }

    override fun onSpeechStart(p0: String?) {
        Log.d("wy", "onSpeechStart-->$p0")
    }

    override fun onSynthesizeDataArrived(p0: String?, p1: ByteArray?, p2: Int) {
        Log.d("wy", "onSynthesizeDataArrived-->$p0,$p2")
    }

    override fun onError(p0: String?, p1: SpeechError?) {
        Log.d("wy", "onError-->$p0,$p1")
    }


}
