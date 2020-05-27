package com.bringg.android.example.driversdk.tasklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import driver_sdk.models.Task

class TaskListAdapter(fragment: Fragment, private val taskList: LiveData<List<Task>>, private val taskViewHolderClickListener: TaskViewHolder.ClickListener) : RecyclerView.Adapter<TaskViewHolder>() {

    init {
        taskList.observe(fragment, Observer { notifyDataSetChanged() })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_task, parent, false), taskViewHolderClickListener)
    }

    override fun getItemCount(): Int {
        val taskList = taskList.value ?: return 0
        return taskList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(taskList.value!![position])
    }
}
