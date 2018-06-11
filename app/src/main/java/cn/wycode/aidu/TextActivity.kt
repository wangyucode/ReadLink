package cn.wycode.aidu

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_text.*

class TextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

        btn_read.setOnClickListener {
            val intent = Intent(this,PlayService::class.java)
            intent.putExtra("text",editText.text)
            startService(intent)
        }
    }
}
