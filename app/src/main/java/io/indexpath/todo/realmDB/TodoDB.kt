package io.indexpath.todo.realmDB

import io.realm.RealmObject
import io.realm.annotations.RealmClass
import java.util.*

/**
 * Todo
 *
 * Created by yuki on 2018. 6. 22.
 */

@RealmClass
open class TodoList: RealmObject() {

    var id: Date? = null
    var owner: String? = null
    var cDate: String = ""
    var content: String? = null
    var isFinish: Boolean = false

}