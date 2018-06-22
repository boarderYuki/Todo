package io.indexpath.todo.fragment

import android.content.Context
import android.net.Uri
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
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var adapter: TodoAdapter
    private var userTodo: RealmResults<TodoDB>? = null
    private var todoRealmManager = TodoRealmManager()

    var getIdFromMyPref = ""
    //private var todoRealmManager = TodoRealmManager()
    //private var userTodo: RealmResults<TodoDTO>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


//        Log.d(TAG, "DrawerActiyity ${(activity as MainActivity).getUserID()!!}")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        //val myPref = activity.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val myPref = getActivity()!!.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        getIdFromMyPref = myPref.getString("id", "")
        Log.d(TAG, "inside fragment : $getIdFromMyPref")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todolist, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initTodoAdapter()

        /** 투두리스트 가져오기 */
        getTodoList()
    }

    /** 투두리스트 가져오기 */
    private fun getTodoList(){
        try {
            //userTodo = todoRealmManager.find(getIdFromMyPref,"owner",TodoDB::class.java)
            userTodo = todoRealmManager.findAll(getIdFromMyPref,"owner",TodoDB::class.java).sort("id", Sort.DESCENDING)
            //userTodo = todoRealmManager.findAll((activity as MainDrawerActivity).getUserID()!!, "userID", TodoDTO::class.java)
            adapter.setDataList(userTodo)
            adapter.notifyDataSetChanged()
        }catch (e: NullPointerException){
            e.printStackTrace()
        }
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


    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
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
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                TodoListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }


        private val TAG = "Todo"

        var loginUserName : String = ""

//        val config = RealmConfiguration.Builder().name("person.realm").build()
//        val realm = Realm.getInstance(config)
//        var todoLists : RealmResults<TodoList>? = null

    }
}
