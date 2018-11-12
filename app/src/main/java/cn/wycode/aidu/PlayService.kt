package cn.wycode.aidu

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Button
import android.widget.RemoteViews
import com.baidu.tts.client.*


const val ACTION_PLAYER_EVENT = "ACTION_PLAYER_EVENT"
const val EXTRA_PLAYER_WHAT = "EXTRA_PLAYER_WHAT"
const val EXTRA_PLAYER_WHAT_SYN_FINISH = 1
const val EXTRA_PLAYER_WHAT_SPEECH_START = 2
const val EXTRA_PLAYER_WHAT_SPEECH_FINISH = 3
const val EXTRA_PLAYER_WHAT_SYN_START = 4
const val EXTRA_PLAYER_PARAM_1 = "EXTRA_PLAYER_PARAM_1"
const val EXTRA_PLAYER_PARAM_2 = "EXTRA_PLAYER_PARAM_2"
const val NOTIFICATION_CHANNEL_ID = "PlayingStatus"

class PlayService : Service(), SpeechSynthesizerListener {


    val AppId = "11382258"
    val AppKey = "sH8rGkPvBgjVPEsn03mGf3bT"
    val AppSecret = "Ec0aUGa8kgKlnKkclNcBScKSqDuRu2vy"

    private var mSpeechSynthesizer: SpeechSynthesizer = SpeechSynthesizer.getInstance()

    var localBroadcastManager = LocalBroadcastManager.getInstance(this)

    lateinit var textPlayingArray: List<String>

    var isPlaying = false
    var currentReadText = ""
    var title = "停止中"

    lateinit var notificationManager: NotificationManagerCompat

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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stop()

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
        mSpeechSynthesizer.auth(TtsMode.ONLINE)
        mSpeechSynthesizer.initTts(TtsMode.ONLINE)
    }

    fun speakText(text: String, title: String) {
        this.title = title
        val bags = getBags(text)
        mSpeechSynthesizer.batchSpeak(bags)
        isPlaying = true
    }

    private fun getBags(text: String): List<SpeechSynthesizeBag> {
        textPlayingArray = text.split('!', '！', '。', '\t', '\n', '\r', '？', '：')
                .filter { it.isNotBlank() }
        return textPlayingArray.mapIndexed { index, s ->
            val bag = SpeechSynthesizeBag()
            bag.text = s
            bag.utteranceId = index.toString()
            bag
        }
    }


    fun stop() {
        mSpeechSynthesizer.stop()
        val intent = Intent(ACTION_PLAYER_EVENT)
        intent.putExtra(EXTRA_PLAYER_WHAT, EXTRA_PLAYER_WHAT_SPEECH_FINISH)
        localBroadcastManager.sendBroadcast(intent)
        isPlaying = false
        notificationManager.cancel(0)
    }


    override fun onSynthesizeStart(p0: String?) {
        val intent = Intent(ACTION_PLAYER_EVENT)
        intent.putExtra(EXTRA_PLAYER_WHAT, EXTRA_PLAYER_WHAT_SYN_START)
        localBroadcastManager.sendBroadcast(intent)
    }

    override fun onSpeechFinish(p0: String?) {
        if (p0?.toInt() == textPlayingArray.size - 1) {
            val intent = Intent(ACTION_PLAYER_EVENT)
            intent.putExtra(EXTRA_PLAYER_WHAT, EXTRA_PLAYER_WHAT_SPEECH_FINISH)
            localBroadcastManager.sendBroadcast(intent)
            isPlaying = false
            notificationManager.cancel(0)
        }
    }

    override fun onSpeechProgressChanged(p0: String?, p1: Int) {
        //Log.d("wy", "onSpeechProgressChanged-->$p0,$p1")
    }

    override fun onSynthesizeFinish(p0: String?) {
        val intent = Intent(ACTION_PLAYER_EVENT)
        intent.putExtra(EXTRA_PLAYER_WHAT, EXTRA_PLAYER_WHAT_SYN_FINISH)
        localBroadcastManager.sendBroadcast(intent)
    }

    override fun onSpeechStart(p0: String?) {
        val intent = Intent(ACTION_PLAYER_EVENT)
        val index = p0?.toInt() ?: 0
        intent.putExtra(EXTRA_PLAYER_WHAT, EXTRA_PLAYER_WHAT_SPEECH_START)
        currentReadText = textPlayingArray[index]
        intent.putExtra(EXTRA_PLAYER_PARAM_1, title)
        intent.putExtra(EXTRA_PLAYER_PARAM_2, currentReadText)
        localBroadcastManager.sendBroadcast(intent)
        showNotification()
    }


    override fun onSynthesizeDataArrived(p0: String?, p1: ByteArray?, p2: Int) {
        //Log.d("wy", "onSynthesizeDataArrived-->$p0,$p2")
    }

    override fun onError(p0: String?, p1: SpeechError?) {
        Log.e("wy", "onError-->$p0,$p1")
    }


    private fun showNotification() {
        createNotificationChannel()
        val notificationView = RemoteViews(packageName, R.layout.view_player_layout)
        notificationView.setCharSequence(R.id.text_title, "setText", title)
        notificationView.setCharSequence(R.id.text_content, "setText", currentReadText)
        notificationView.setInt(R.id.btn_play_or_pause, "setBackgroundResource", R.drawable.stop)
        val intent = Intent(this, PlayService::class.java).putExtra(EXTRA_PLAYER_WHAT, EXTRA_PLAYER_WHAT_SPEECH_FINISH)
        val stopPendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationView.setOnClickPendingIntent(R.id.btn_play_or_pause, stopPendingIntent)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setSound(null)
                .setContent(notificationView)
                .setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.ic_launcher))
                .build()

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, notification)
    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "播放状态"
            val description = "显示AI读文的播放状态，并可以在通知栏直接停止播放"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            channel.description = description
            channel.setSound(null,null)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
