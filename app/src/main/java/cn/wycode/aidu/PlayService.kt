package cn.wycode.aidu

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class PlayService : Service() {

    inner class PlayServiceBinder:Binder(){
        fun getService():PlayService{
            return this@PlayService
        }
    }

    val binder = PlayServiceBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }


}
