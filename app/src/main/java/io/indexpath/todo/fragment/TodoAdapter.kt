package io.indexpath.todo.fragment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import io.indexpath.todo.R
import io.indexpath.todo.realmDB.TodoDB

/**
 * Todo
 *
 * Created by yuki on 2018. 6. 22.
 */

/** 바인더와 어댑터를 연결 */
interface OnItemClickListener {
    fun checkBoxClick(isFinish: Boolean, position: Int)
    fun itemDeleteClick(position: Int)
}

class TodoAdapter(val context: Context, val dataList: List<TodoDB>?, val listener: OnItemClickListener) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var todoLists : MutableList<TodoDB>? = null

    /**　뷰홀더 생성 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = inflater.inflate(R.layout.cell_layout, parent,false)
        return ViewHolder(view)
    }

    /**　리스트 갯수 */
    override fun getItemCount(): Int {
        return todoLists!!.count()
    }

    /**　바인딩 */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //var view = holder as ViewHolder
        holder.createDateText!!.text = todoLists!![position]!!.cDate
        holder.textview!!.text = todoLists!![position]!!.content.toString()
        holder.cellCheckBox!!.setChecked(todoLists!![position]!!.isFinish)

        var cb = holder.cellCheckBox
        var btnDel = holder.btnDel

        /** 어댑터로 연결 됨 */
        cb!!.setOnClickListener {
            listener.checkBoxClick( cb.isChecked, position)
            notifyDataSetChanged()
        }

        btnDel!!.setOnClickListener{
            listener.itemDeleteClick(position)
            notifyDataSetChanged()
            Log.d(TAG, " 삭제버튼 클릭 : ${position} ")
        }
    }

    fun setDataList(dataList: List<TodoDB>?){
        if(dataList == null){
            return
        }
        this@TodoAdapter.todoLists = dataList.toMutableList()
    }

    /**　뷰홀더 연결 */
    class ViewHolder(view: View?) : RecyclerView.ViewHolder(view) {
        var createDateText : TextView? = null
        var textview : TextView? = null
        var cellCheckBox : CheckBox? = null
        var btnDel : Button? = null

        init {
            createDateText = view!!.findViewById(R.id.createDate)
            textview = view.findViewById(R.id.todoContent)
            cellCheckBox = view.findViewById(R.id.cellCheckBox)
            btnDel = view.findViewById(R.id.btnDel)
        }

    }

    companion object {
        private val TAG = "Todo"
    }
}