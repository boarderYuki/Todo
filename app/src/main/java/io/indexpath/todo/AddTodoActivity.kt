package io.indexpath.todo

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import es.dmoral.toasty.Toasty
import io.indexpath.todo.fragment.TodoListFragment
import io.indexpath.todo.realmDB.TodoDB
import io.indexpath.todo.realmDB.TodoRealmManager
import kotlinx.android.synthetic.main.activity_add_todo.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class AddTodoActivity : AppCompatActivity() {

    private var getIdFromMyPref = ""

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
        getIdFromMyPref = myPref.getString("id", "")

        Log.d(TAG, "addTodo login id : $getIdFromMyPref")

        initListener()


    }





//    private fun cancelClicked() {
//        cancelButton.setOnClickListener {
//
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//            overridePendingTransition(R.anim.activity_slide_enter, R.anim.activity_slide_exit)
//
//            Log.d(TAG, "clicked addTodoButton : $getIdFromMyPref")
//        }
//    }

    private fun initListener(){
        doneButton.setOnClickListener{
//            var todoRealmManager = TodoRealmManager()
//            var todo = TodoDTO()
//            todo.todoID = System.currentTimeMillis()
//            todo.userID = userID
//            todo.content =  editToDoText.text.toString()
//            todo.isTodo = false
//            todoRealmManager.insertTodo(TodoDTO::class.java, todo)
//            setResult(Activity.RESULT_OK)
//            finish()


            if (todoInputText.text.isNotBlank()) {
                val finalTodoText = removeExtraWhiteSpaces(todoInputText.getText().toString())

                // 타임스탬프 미니멈 API 26 필요함
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                val formatted = current.format(formatter)

                /** 렘에 투두 목록을 유저아이디 owner로 저장 */
                var todoRealmManager = TodoRealmManager()
                var todoDB = TodoDB()
                todoDB.id = Date()
                todoDB.owner = getIdFromMyPref
                todoDB.cDate = formatted
                todoDB.content = finalTodoText
                todoDB.isFinish = false
                todoRealmManager.insertTodo(TodoDB::class.java, todoDB)
                setResult(Activity.RESULT_OK)


                Log.d(TAG, finalTodoText)
                Toasty.success(this, "새로운 목록이 추가되었습니다. $finalTodoText", Toast.LENGTH_SHORT, true).show()
//                val i = Intent(this, MainActivity::class.java)
//                startActivity(i)

//                val currentFragment = getActivity(this)!!.getFragmentManager().findFragmentById(R.id.frameLayout)
//                if (currentFragment == TodoListFragment::class.java) {
//                    val fragTransaction = (getActivity())!!.getFragmentManager().beginTransaction()
//                    fragTransaction.detach(currentFragment)
//                    fragTransaction.attach(currentFragment)
//                    fragTransaction.commit()
//                }

                //val currentFragment = getActivity().getFragmentManager().findFragmentById(R.id.frameLayout)
                val fragmentManager = supportFragmentManager
                //fragmentManager.beginTransaction().replace(R.id.flcontent, myFragment).setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()




                var myFragment = (TodoListFragment::class.java).newInstance() as Fragment
//                val ft = fragmentManager.beginTransaction()

                //fragmentManager.beginTransaction().detach(myFragment).attach(myFragment).commit()
                //fragmentManager.beginTransaction().detach(currentFragment).attach(currentFragment).commit()

                //getSupportFragmentManager().beginTransaction().detach(myFragment).commitNowAllowingStateLoss();
                //getSupportFragmentManager().beginTransaction().attach(myFragment).commitAllowingStateLoss();
                finish()

                overridePendingTransition(R.anim.activity_slide_enter, R.anim.activity_slide_exit)


                /** 데이타가 추가되면 리사이클뷰를 다시 그리는 것 같음 */

//                recyclerView.adapter.notifyDataSetChanged()
//                customDialog.dismiss()

            } else {
                // 공백만 있거나 내용이 없는 경우
                Toasty.error(this, "내용이 없습니다.", Toast.LENGTH_SHORT, true).show()
            }

        }
        cancelButton.setOnClickListener{
//            val i = Intent(this, MainActivity::class.java)
//            startActivity(i)
            finish()
            overridePendingTransition(R.anim.activity_slide_enter, R.anim.activity_slide_exit)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
        Toasty.error(this, "디바이스 백버튼 클릭", Toast.LENGTH_SHORT, true).show()
        //startActivity<MainActivity>()
        finish()
        overridePendingTransition(R.anim.activity_slide_enter, R.anim.activity_slide_exit)
    }



    // 공백 검사
    fun removeExtraWhiteSpaces(value: String): String {

        var result = ""
        var prevChar = ""

        for ( char in value ) {

            if ( (prevChar == " " && char == ' ').not() ) {
                result += char
            }
            prevChar = char.toString()
        }

        return result
    }

    companion object {
        private val TAG = "Todo"
    }
}
