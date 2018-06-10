package cn.wycode.aidu

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainFragment.OnFragmentInteractionListener, AboutFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
                .add(R.id.fl_main_container, aboutFragment, "about")
                .hide(aboutFragment)
                .commit()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
