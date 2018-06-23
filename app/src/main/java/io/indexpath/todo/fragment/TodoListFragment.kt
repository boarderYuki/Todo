package io.indexpath.todo.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.indexpath.todo.R
import io.indexpath.todo.realmDB.TodoDB
import io.indexpath.todo.realmDB.TodoRealmManager
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_todolist.*




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Dashboard.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Dashboard.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TodoListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: Bundle? = null
    private var param1: String? = null
    private var param2: String? = null
    //private var listener: OnFragmentInteractionListener? = null
    private lateinit var adapter: TodoAdapter
    private var userTodo: RealmResults<TodoDB>? = null
    private var todoRealmManager = TodoRealmManager()

    var getIdFromMyPref = ""
    //private var todoRealmManager = TodoRealmManager()
    //private var userTodo: RealmResults<TodoDTO>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getBundle(ARG_PARAM1)
        }

        val myPref = getActivity()!!.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        getIdFromMyPref = myPref.getString("id", "")
        Log.d(TAG, "inside fragment : $getIdFromMyPref")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_todolist, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initTodoAdapter()
        getTodoList()
    }

    private fun initTodoAdapter(){
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = TodoAdapter(activity!!.applicationContext, userTodo, object : OnItemClickListener{
            override fun checkBoxClick(position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun itemDeleteClick(position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

        recyclerView.adapter = adapter

        if (userTodo == null){
            return
        }else{
            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Dashboard.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(args: Bundle): TodoListFragment {
            val fragment = TodoListFragment()
//            val args = Bundle()
//            args.putString(ARG_PARAM1, param1)
            fragment.arguments = args
            return fragment
        }

        private val TAG = "Todo"

    }

    /** 투두리스트 가져오기 */
    private fun getTodoList(){
        try {
            userTodo = todoRealmManager.findAll(getIdFromMyPref,"owner",TodoDB::class.java).sort("id", Sort.DESCENDING)
            adapter.setDataList(userTodo)
            adapter.notifyDataSetChanged()
        }catch (e: NullPointerException){
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(resultCode != Activity.RESULT_OK){
            return
        }

        when (requestCode){
            1 -> {
                userTodo = todoRealmManager.findAll(getIdFromMyPref,"owner",TodoDB::class.java).sort("id", Sort.DESCENDING)
                adapter.setDataList(userTodo)
                adapter.notifyDataSetChanged()
            }
        }


//        if (requestCode == 1) {
//
//            if (resultCode == Activity.RESULT_OK) {
//                Log.d(TAG, "플래그 먼트 onActivityResult")
//
//                userTodo = todoRealmManager.findAll(getIdFromMyPref,"owner",TodoDB::class.java).sort("id", Sort.DESCENDING)
//
////                val ft = getFragmentManager()?.beginTransaction()
////                ft!!.detach(this).attach(this).commit()
//
////val ft = getFragmentManager()!!.beginTransaction()
////                ft.attach(this).commit()
//                //adapter = recyclerView.adapter as TodoAdapter
//
//
////                if (adapter == null) {
////                    Log.d(TAG, "어댑터 없음")
////                } else {
////                    Log.d(TAG, "어댑터 있음")
////                }
//
//                adapter.setDataList(userTodo)
//                adapter.notifyDataSetChanged()
//            }
//            if (resultCode == Activity.RESULT_CANCELED) {
//                //Write your code if there's no result
//            }
//        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }





}
