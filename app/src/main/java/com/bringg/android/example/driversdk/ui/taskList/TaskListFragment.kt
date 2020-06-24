package com.bringg.android.example.driversdk.ui.taskList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.tasklist.TaskListAdapter
import com.bringg.android.example.driversdk.tasklist.TaskViewHolder
import driver_sdk.DriverSdkProvider
import driver_sdk.models.Task
import kotlinx.android.synthetic.main.task_list_fragment.*

class TaskListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.task_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = TaskListAdapter(this, DriverSdkProvider.driverSdk().data.taskList, object : TaskViewHolder.ClickListener {
            override fun onTaskItemClick(task: Task) {
                val args = bundleOf("task_id" to task.getId())
                findNavController().navigate(R.id.action_task_list_to_task_fragment, args)
            }
        })
        rv_task_list.adapter = adapter
    }
}
