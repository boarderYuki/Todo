package io.indexpath.todo


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import es.dmoral.toasty.Toasty
import io.indexpath.todo.fragment.Event
import io.indexpath.todo.fragment.Search
import io.indexpath.todo.fragment.Setting
import io.indexpath.todo.fragment.TodoListFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_bar_main.*
import kotlinx.android.synthetic.main.header.view.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult


class MainActivity : AppCompatActivity() {

    private var mDrawerLayout: DrawerLayout? = null
    private var mToggle: ActionBarDrawerToggle? = null
    lateinit var loginId : String
    private lateinit var todoListFragment: TodoListFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //supportActionBar!!.setTitle("MEMBER REGISTRATION")
        setContentView(R.layout.activity_main)

        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val getIdFromMyPref:String = myPref.getString("id", "")

        mDrawerLayout = drawer
        mToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)
        mDrawerLayout!!.addDrawerListener(mToggle!!)
        val nvDrawer = nv as NavigationView
        mToggle!!.syncState()

        val fragmentManager = supportFragmentManager
        todoListFragment = TodoListFragment()
        fragmentManager.beginTransaction().replace(R.id.flcontent, TodoListFragment()).commit()
        setupDrawerContent(nvDrawer)

        val headerView = nv.getHeaderView(0)
        headerView.headerId.text = getIdFromMyPref

        try {
            supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            supportActionBar!!.setCustomView(R.layout.custom_bar_main)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        } catch (e: Exception) {
            println(e.message)
        }

        //var intentBundle = Bundle()


        addTodoButton.setOnClickListener {
//            val i = Intent(this, AddTodoActivity::class.java)
//            startActivityForResult(i,1)

            startActivityForResult<AddTodoActivity>(1)
            //startActivityForResult(i,1,intentBundle)

            //startActivity<AddTodoActivity>(1)
            //finish()
            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            Log.d(TAG, "clicked addTodoButton : $getIdFromMyPref")
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    fun selectItemDrawer(menuItem: MenuItem) {
        var myFragment: Fragment? = null
        var fragmentClass: Class<*> = TodoListFragment::class.java

        when (menuItem.itemId) {
            R.id.db -> {
                fragmentClass = TodoListFragment::class.java
                addTodoButton.visibility = View.VISIBLE
            }
            R.id.event -> {
                fragmentClass = Event::class.java
                addTodoButton.visibility = View.GONE
            }
            R.id.search -> {
                fragmentClass = Search::class.java
                addTodoButton.visibility = View.GONE
            }
            R.id.settings -> {
                fragmentClass = Setting::class.java
                addTodoButton.visibility = View.GONE
            }
            //else -> fragmentClass = TodoListFragment::class.java
            R.id.logout -> {
                val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
                val editor = myPref.edit()
                editor.putBoolean("autoLogin", false)
                editor.apply()

                startActivity<LoginActivity>()
                finish()
                // to previous
                overridePendingTransition(R.anim.activity_slide_enter, R.anim.activity_slide_exit)

                Toasty.success(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT, true).show()
            }
        }

        try {
            myFragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.flcontent, myFragment).commit()
        menuItem.isChecked = true
        customTitle.setText(menuItem.title)

        mDrawerLayout!!.closeDrawers()

    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { item ->
            selectItemDrawer(item)
            true
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if(resultCode != Activity.RESULT_OK){
//            return
//        }

        if (resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult")
            todoListFragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun getUserID() : String? {
        return loginId
    }

//    private lateinit var adapter: TodoAdapter
//    private var userTodo: RealmResults<TodoDB>? = null
    override fun onResume() {
        super.onResume()

    }

    companion object {
        private val TAG = "Todo"
    }
}
