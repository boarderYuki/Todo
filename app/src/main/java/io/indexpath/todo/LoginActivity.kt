package io.indexpath.todo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Window
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import es.dmoral.toasty.Toasty
import io.indexpath.todo.realmDB.Person
import io.indexpath.todo.realmDB.UserRealmManager
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity() {

    //lateinit var realm:Realm

    var realmManager = UserRealmManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

//        try {
//            supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
//            supportActionBar!!.setCustomView(R.layout.custom_bar)
//        } catch (e: Exception) {
//            println(e.message)
//        }

        //Realm.init(this)
        setContentView(R.layout.activity_login)


        Log.d(TAG, "path: " + realmManager.realm.path)

//        val config = RealmConfiguration.Builder().name("person.realm").build()
//        realm = Realm.getInstance(config)
        //getLastMember()

        /** 로그인 버튼관련 옵저버
         * 아이디와 패스워드 필드가 비워져있지만 않으면 버튼이 활성화 됨*/
        val observableId = RxTextView.textChanges(todoInputText)
                .map { t -> t.toString().isNotEmpty() }

        val observablePw = RxTextView.textChanges(editTextPassword)
                .map { t -> t.toString().isNotEmpty() }

        // 아이디, 패스워드 필드에 모두 값이 있는 경우 버튼 컬러 변경하고 클릭가능하게 한다
        val signInEnabled: Observable<Boolean> = Observable.combineLatest(
                observableId, observablePw, BiFunction { i, p -> i && p }
        )


        signInEnabled.distinctUntilChanged()
                .subscribe { enabled -> buttonLogOut.isEnabled = enabled }
        signInEnabled.distinctUntilChanged()
                .map { b -> if (b) R.color.buttonOn else R.color.buttonOff }
                .subscribe { color -> buttonLogOut.backgroundTintList =
                        ContextCompat.getColorStateList(this, color) }


        /** 자동 로그인 셋팅
         * 이전 로그인시에 자동로그인 체크 여부를 확인해서 체크박스 설정 */
        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val autoLogin = myPref.getBoolean("autoLogin", false)

        checkAutoLogin.isChecked = autoLogin

        /** 자동 로그인으로 로그인 하기*/
        if (autoLogin) {

            val getIdFromMyPref:String = myPref.getString("id", "")
            val getPasswordFromMyPref = myPref.getString("password", "")
            val user = realmManager.findAll(getIdFromMyPref, "userId", Person::class.java)

            if (!user.isEmpty() && user.last()?.password == getPasswordFromMyPref){
                // 결과 화면으로 넘기고 종료
                startActivity<MainActivity>()
                finish()
            }

        }

        /** 로그인 버튼 클릭
         * 아이디 값으로 디비 검색해서 패스워드 비교 */
        buttonLogOut.setOnClickListener {
            //Login()
            Log.d(TAG,"로그인 클릭")
            //val config = RealmConfiguration.Builder().name("person.realm").build()
            //realm = Realm.getInstance(config)
            //realm.beginTransaction()

            //val user = realm.where(Person::class.java).equalTo("userId",editTextId.text.toString().trim()).findAll()
            val user = realmManager.findAll(todoInputText.text.toString().trim(), "userId", Person::class.java)
            Log.d(TAG,"유저 카운트  : ${user.count()}")

            if (user.isEmpty()) {

                Log.d(TAG,"유저 없음")
                Toasty.error(this, "유저 아이디 없음", Toast.LENGTH_SHORT, true).show()

                todoInputText.text = null
                editTextPassword.text = null

            } else {

                val lastUser = user.last()
                if (editTextPassword.text.toString() != lastUser?.password.toString()) {
                    Toasty.error(this, "패스워드 틀림", Toast.LENGTH_SHORT, true).show()

                } else {

                    /** 자동 로그인
                     * 체크되어 있으면 SharedPreferences에서 체크여부와 아이디, 패스워드 정보 가져와서 자동으로 로그인 하고 결과 화면으로 이동 */
                    val editor = myPref.edit()

                    if (checkAutoLogin.isChecked) {
                        editor.putBoolean("autoLogin", true)


                    } else {
                        editor.putBoolean("autoLogin", false)
                    }

                    editor.putString("id", todoInputText.text.toString())
                    editor.putString("password", editTextPassword.text.toString())

                    editor.apply()

                    Toasty.success(this, "로그인 성공", Toast.LENGTH_SHORT, true).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("id", todoInputText.text.toString())
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)

                }
            }

        }

        /** 회원 가입 버튼 클릭 */
        buttonSignUp.setOnClickListener {
            startActivity<JoinActivity>()
            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
        }



    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private val TAG = "Todo"
    }

}
