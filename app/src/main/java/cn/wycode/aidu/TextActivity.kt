package cn.wycode.aidu

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.TtsMode
import kotlinx.android.synthetic.main.activity_text.*

class TextActivity : AppCompatActivity() {

    val AppId = "11382258"
    val AppKey = "sH8rGkPvBgjVPEsn03mGf3bT"
    val AppSecret = "Ec0aUGa8kgKlnKkclNcBScKSqDuRu2vy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

        btn_read.setOnClickListener {
            val intent = Intent(this,PlayService::class.java)
            intent.putExtra("text",editText.text.toString())
            startService(intent)

//            var mSpeechSynthesizer: SpeechSynthesizer = SpeechSynthesizer.getInstance()
//            mSpeechSynthesizer.setContext(this)
//            //mSpeechSynthesizer.setSpeechSynthesizerListener(this) //listener是SpeechSynthesizerListener 的实现类，需要实现您自己的业务逻辑。SDK合成后会对这个类的方法进行回调。
//            mSpeechSynthesizer.setAppId(AppId)
//            mSpeechSynthesizer.setApiKey(AppKey, AppSecret)
//            mSpeechSynthesizer.auth(TtsMode.ONLINE)
//            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0")
//            mSpeechSynthesizer.initTts(TtsMode.ONLINE)
//            mSpeechSynthesizer.speak(editText.text.toString())
        }
    }
}
