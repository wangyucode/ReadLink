package cn.wycode.aidu

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_text.*


class TextActivity : AppCompatActivity() {

    var mService: PlayService? = null
    var mBound = false

    var loadingAlert: AlertDialog? = null

    private lateinit var localBroadcastManager: LocalBroadcastManager

    inner class PlayReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("wy", "onReceive")
            when (intent?.getIntExtra(EXTRA_PLAYER_WHAT, 0)) {
                EXTRA_PLAYER_WHAT_SYN_FINISH -> loadingAlert?.dismiss()
            }
        }
    }

    private var playReceiver = PlayReceiver()

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
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)

        view_player.setOnPlayOrPauseClickedListener(View.OnClickListener {
            if (mBound) {
                mService?.speakText(editText.text.toString())
                if (loadingAlert == null)
                    loadingAlert = AlertDialog.Builder(this)
                            .setTitle("正在生成语音...")
                            .setView(ProgressBar(this))
                            .create()

                loadingAlert?.show()
            }
        })

        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(playReceiver, IntentFilter(ACTION_PLAYER_EVENT))
    }


    override fun onDestroy() {
        super.onDestroy()
        localBroadcastManager.unregisterReceiver(playReceiver)
    }


}
