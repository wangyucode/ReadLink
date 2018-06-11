package cn.wycode.aidu

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.TtsMode


class PlayService : Service(), SpeechSynthesizerListener {

    val AppId = "11382258"
    val AppKey = "sH8rGkPvBgjVPEsn03mGf3bT"
    val AppSecret = "Ec0aUGa8kgKlnKkclNcBScKSqDuRu2vy"

    private var mSpeechSynthesizer: SpeechSynthesizer = SpeechSynthesizer.getInstance()

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

    val binder = PlayServiceBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }


    override fun onCreate() {
        super.onCreate()
        mSpeechSynthesizer.auth(TtsMode.ONLINE)
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0")
        mSpeechSynthesizer.initTts(TtsMode.ONLINE)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            mSpeechSynthesizer.speak(intent.getStringExtra("text"))
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onSynthesizeStart(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSpeechFinish(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSpeechProgressChanged(p0: String?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSynthesizeFinish(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSpeechStart(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSynthesizeDataArrived(p0: String?, p1: ByteArray?, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(p0: String?, p1: SpeechError?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
