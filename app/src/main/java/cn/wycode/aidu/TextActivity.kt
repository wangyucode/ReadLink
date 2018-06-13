package cn.wycode.aidu

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_text.*


class TextActivity : AppCompatActivity() {

    lateinit var mService: PlayService
    var mBound = false

    lateinit var loadingAlert: AlertDialog

    private lateinit var localBroadcastManager: LocalBroadcastManager

    private var playReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getIntExtra(EXTRA_PLAYER_WHAT, 0)) {
                EXTRA_PLAYER_WHAT_SYN_START -> {

                }
                EXTRA_PLAYER_WHAT_SPEECH_START -> {
                    if (loadingAlert.isShowing)
                        loadingAlert.dismiss()
                    view_player.setTitleText(intent.getStringExtra(EXTRA_PLAYER_PARAM_1))
                    view_player.setContentText(intent.getStringExtra(EXTRA_PLAYER_PARAM_2))
                    view_player.isPlaying = true
                }

                EXTRA_PLAYER_WHAT_SPEECH_FINISH -> {
                    view_player.setTitleText("停止中")
                    view_player.setContentText(getString(R.string.no_content))
                    view_player.isPlaying = false
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
            if (mService.isPlaying) {
                view_player.setTitleText("播放文字中")
                view_player.setContentText(mService.currentReadText)
            }
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
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)

        loadingAlert = AlertDialog.Builder(this)
                .setTitle("处理中...")
                .setView(ProgressBar(this))
                .create()
        view_player.setOnPlayOrPauseClickedListener(View.OnClickListener {
            if (mBound) {
                if (view_player.isPlaying) {
                    mService.stop()
                } else {
                    if (editText.text.isBlank()) {
                        Toast.makeText(this, "没有可以朗读的内容", Toast.LENGTH_SHORT).show();
                        return@OnClickListener
                    }
                    mService.speakText(editText.text.toString(), "正在朗读文字")
                    loadingAlert.show()
                }

            }
        })

        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(playReceiver, IntentFilter(ACTION_PLAYER_EVENT))
    }


    override fun onDestroy() {
        super.onDestroy()
        unbindService(mConnection)
        localBroadcastManager.unregisterReceiver(playReceiver)
    }


}
