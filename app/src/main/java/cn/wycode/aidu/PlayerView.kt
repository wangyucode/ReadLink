package cn.wycode.aidu

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_player_layout.view.*

class PlayerView: LinearLayout{


    lateinit var view: View

    var isPlaying = false
        set(value) {
            field = value
            if(value) {
                view.btn_play_or_pause.setBackgroundResource(R.drawable.stop)
            }else{
                view.btn_play_or_pause.setBackgroundResource(R.drawable.play)
            }
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context,attrs)

    override fun onFinishInflate() {
        super.onFinishInflate()
        view = LayoutInflater.from(context).inflate(R.layout.view_player_layout, this)
    }

    fun setOnPlayOrPauseClickedListener(listener: OnClickListener) {
        view.btn_play_or_pause.setOnClickListener(listener)
    }

    fun setContentText(text: String) {
        view.text_content.text = text
    }


    fun setTitleText(text: String){
        view.text_title.text = text
    }


}