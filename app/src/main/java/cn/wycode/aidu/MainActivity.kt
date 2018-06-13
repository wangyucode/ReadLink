package cn.wycode.aidu

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.BottomNavigationView
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainFragment.OnFragmentInteractionListener, AboutFragment.OnFragmentInteractionListener {

    lateinit var mService: PlayService
    var mBound = false

    private lateinit var localBroadcastManager: LocalBroadcastManager

    private var playReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getIntExtra(EXTRA_PLAYER_WHAT, 0)) {
                EXTRA_PLAYER_WHAT_SYN_START -> {

                }
                EXTRA_PLAYER_WHAT_SPEECH_START -> {
                    view_player.setTitleText(intent.getStringExtra(EXTRA_PLAYER_PARAM_1))
                    view_player.setContentText(intent.getStringExtra(EXTRA_PLAYER_PARAM_2))
                    view_player.isPlaying = true
                    view_player.visibility = View.VISIBLE
                }

                EXTRA_PLAYER_WHAT_SPEECH_FINISH -> {
                    view_player.setTitleText("停止中")
                    view_player.setContentText(getString(R.string.no_content))
                    view_player.isPlaying = false
                    view_player.visibility = View.GONE
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
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onFragmentInteraction(uri: Uri) {

    }


    private val mainFragment = MainFragment.newInstance("a", "b")
    private val aboutFragment = AboutFragment.newInstance("a", "b")

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val transaction = supportFragmentManager.beginTransaction()
        when (item.itemId) {
            R.id.navigation_home -> {
                transaction.hide(aboutFragment)
                transaction.show(mainFragment)
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_about -> {
                transaction.hide(mainFragment)
                transaction.show(aboutFragment)
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
                .add(R.id.fl_main_container, mainFragment, "main")
                .add(R.id.fl_main_container, aboutFragment, "setting")
                .hide(aboutFragment)
                .commit()

        val intent = Intent(this, PlayService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        view_player.setOnPlayOrPauseClickedListener(View.OnClickListener {
            if (mBound) {
                if (view_player.isPlaying) {
                    mService.stop()
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
