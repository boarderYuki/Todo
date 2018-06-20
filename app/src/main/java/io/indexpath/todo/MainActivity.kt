package io.indexpath.todo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import io.realm.Realm

class MainActivity : AppCompatActivity() {

    lateinit var realm:Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

//        try {
//            supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
//            supportActionBar!!.setCustomView(R.layout.custom_bar)
//        } catch (e: Exception) {
//            println(e.message)
//        }


        setContentView(R.layout.activity_main)

    }



    companion object {
        private val TAG = "Todo"
    }
}
