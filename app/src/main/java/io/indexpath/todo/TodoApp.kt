package io.indexpath.todo

import android.app.Application
import io.realm.Realm

/**
 * Todo
 *
 * Created by yuki on 2018. 6. 24.
 */

class TodoApp : Application(){
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
    }
}