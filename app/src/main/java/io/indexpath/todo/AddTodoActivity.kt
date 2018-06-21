package io.indexpath.todo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem





class AddTodoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)
        try {
            //supportActionBar!!.setTitle("Add To do")
            supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            supportActionBar!!.setCustomView(R.layout.custom_bar_addtodo)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //title = "Add to do"

        } catch (e: Exception) {
            println(e.message)
        }

        /** 로그인 아이디 가져오기 */
        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val getIdFromMyPref:String = myPref.getString("id", "")

        Log.d(TAG, "addTodo login id : $getIdFromMyPref")
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.activity_slide_enter, R.anim.activity_slide_exit)
    }


    companion object {
        private val TAG = "Todo"
    }
}
