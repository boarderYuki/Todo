package io.indexpath.todo

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mDrawerLayout: DrawerLayout? = null
    private var mToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //supportActionBar!!.setTitle("MEMBER REGISTRATION")
        setContentView(R.layout.activity_main)

        //mDrawerLayout = findViewById<View>(R.id.drawer)
        mDrawerLayout = drawer
        mToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)
        mDrawerLayout!!.addDrawerListener(mToggle!!)
        val nvDrawer = nv as NavigationView
        mToggle!!.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setupDrawerContent(nvDrawer)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    fun selectItemDrawer(menuItem: MenuItem) {
        var myFragment: Fragment? = null
        val fragmentClass: Class<*>
        when (menuItem.itemId) {
            R.id.db -> fragmentClass = Dashboard::class.java
            R.id.event -> fragmentClass = Event::class.java
            R.id.search -> fragmentClass = Search::class.java
            R.id.settings -> fragmentClass = Setting::class.java
            else -> fragmentClass = Dashboard::class.java
        }
        try {
            myFragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.flcontent, myFragment).commit()
        menuItem.isChecked = true
        title = menuItem.title
        mDrawerLayout!!.closeDrawers()
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { item ->
            selectItemDrawer(item)
            true
        }
    }



    companion object {
        private val TAG = "Todo"
    }
}
