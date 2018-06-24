package io.indexpath.todo.realmDB

import io.realm.RealmConfiguration
import io.realm.RealmModel

/**
 * Created by kcs on 2018. 5. 29..
 */
class UserRealmManager : RealmManager("UserDTO.realm") {
    fun <T: RealmModel, E: Person>insertUser(targetDto: Class<T>, rPerson: E){

        realm.beginTransaction()
        val maxId = realm.where(targetDto).max("id")
        val nextId = if (maxId == null) 1 else maxId.toInt() + 1

        val person = realm.createObject(targetDto, nextId)

        //val number = realm.where(targetDto).count() + 1

        //val person = realm.createObject(targetDto, number)

        if (person is Person) {
            person.userId = rPerson.userId
            person.email = rPerson.email
            person.password = rPerson.password
            person.profilePic = rPerson.profilePic
        }

        realm.commitTransaction()
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