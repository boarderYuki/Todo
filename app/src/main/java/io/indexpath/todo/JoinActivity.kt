package io.indexpath.todo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class JoinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
