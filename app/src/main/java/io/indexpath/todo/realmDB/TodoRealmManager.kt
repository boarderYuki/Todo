package io.indexpath.todo.realmDB

import io.realm.RealmConfiguration
import io.realm.RealmModel
import io.realm.RealmResults







/**
 * Created by kcs on 2018. 5. 29..
 */
class TodoRealmManager : RealmManager("TodoDTO.realm") {

    fun <T: RealmModel, E: TodoDB>insertTodo(targetDto: Class<T>, dto: E){

        realm.beginTransaction()

        //PrimaryKey 증가해서 넣어주는 것이 중요!!
        val maxId = realm.where(targetDto).max("todoID")
        val nextId = if (maxId == null) 1 else maxId.toInt() + 1

        val account = realm.createObject(targetDto, nextId)
        if(account is TodoDB){
            account.id = dto.id
            account.owner = dto.owner
            account.cDate = dto.cDate
            account.content = dto.content
            account.isFinish = dto.isFinish
        }
        realm.commitTransaction()
    }


    fun <T: TodoDB> updateCheckUseData(isCheck: Boolean, position:Int, dataList: RealmResults<T>): RealmResults<T>{
        if(position >= 0 && position < dataList.size){
            realm.beginTransaction()
            dataList?.get(position)?.isFinish = isCheck
            realm.commitTransaction()
        }

        return dataList
    }

    override fun clear(){
        val config = RealmConfiguration.Builder().name(name).build()
        if (config != null) {
            realm.beginTransaction()

//            realm.where(UserDTO::class.java).findAll().deleteAllFromRealm()
            realm.delete(Person::class.java)

            realm.commitTransaction()
            realm.close()
        }
    }
}