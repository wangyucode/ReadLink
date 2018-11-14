package cn.wycode.aidu

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import androidx.versionedparcelable.ParcelField
import cn.wycode.aidu.subscription.SearchResultActivity
import kotlinx.android.synthetic.main.activity_subscription.*
import org.jsoup.Jsoup
import java.io.Serializable

class SubscriptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)
        setSupportActionBar(my_toolbar)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the options menu from XML
        val inflater = menuInflater
        inflater.inflate(R.menu.subscription_menu, menu)

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.action_search).actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            queryHint = "搜索公众号"
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            val mSearchAutoComplete = findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
            mSearchAutoComplete.setTextColor(Color.WHITE)
            mSearchAutoComplete.setHintTextColor(Color.parseColor("#cccccc"))

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d("onQueryTextSubmit->", query)
                    if (query.isNullOrBlank()) {
                        return false
                    } else {
                        searchWechatSubscription(query!!)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })
        }

        return true
    }

    fun searchWechatSubscription(query: String) {
        SearchTask(this).execute(query)
    }
}

class SearchTask(val context: Context) : AsyncTask<String, Int, ArrayList<WeChatSubscription>>() {
    override fun doInBackground(vararg query: String): ArrayList<WeChatSubscription> {
        val url = "https://weixin.sogou.com/weixin?type=1&query=${query[0]}&ie=utf8&s_from=input&_sug_=y&_sug_type_="
        val document = Jsoup.connect(url).get()
        val list = document.selectFirst("ul.news-list2")
        val listItems = list.children()
        val resultList = ArrayList<WeChatSubscription>(listItems.size)
        for (listItem in listItems) {
            val img = listItem.selectFirst("div.img-box > a > img").attr("src")
            val name = listItem.selectFirst("p.tit > a").text()
            resultList.add(WeChatSubscription(name, img))
        }
        return resultList
    }

    override fun onPostExecute(resultList: ArrayList<WeChatSubscription>) {
        Log.d("result->", resultList.toString())
        if (resultList.size > 0) {
            context.startActivity(Intent(context, SearchResultActivity::class.java).putExtra("data", resultList as Serializable))
        }
    }

}


data class WeChatSubscription(val name: String,
                              val img: String) : Serializable
