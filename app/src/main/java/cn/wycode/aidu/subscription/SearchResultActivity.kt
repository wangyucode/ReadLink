package cn.wycode.aidu.subscription

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import cn.wycode.aidu.R
import cn.wycode.aidu.WeChatSubscription
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_search_result.*

class SearchResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        lv_subscription.adapter = SubscriptionAdapter(this,intent.getSerializableExtra("data") as ArrayList<WeChatSubscription>)
    }
}

class SubscriptionAdapter(val context: Context, val data: ArrayList<WeChatSubscription>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent,false)
            val image = itemView!!.findViewById<ImageView>(R.id.iv_gzh_img)
            val name = itemView.findViewById<TextView>(R.id.tv_gzh_name)
            itemView.tag = ViewHolder(image, name)
        }
        val viewHolder = itemView.tag as ViewHolder
        val value = data[position]
        viewHolder.name.text = value.name
        Glide.with(context).load("https:"+value.img).into(viewHolder.image)
        return itemView
    }

    override fun getItem(position: Int): WeChatSubscription {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }

    data class ViewHolder(val image:ImageView,val name: TextView)

}
