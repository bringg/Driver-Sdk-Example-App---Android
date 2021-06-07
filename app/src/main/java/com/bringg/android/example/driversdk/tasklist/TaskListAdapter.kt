package com.bringg.android.example.driversdk.tasklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import com.bringg.android.example.driversdk.BringgSdkViewModel
import com.bringg.android.example.driversdk.databinding.ListItemTaskBinding
import com.bringg.android.example.driversdk.tasklist.TaskViewHolder.ClickListener
import driver_sdk.models.Task

class TaskListAdapter(
    private val viewModel: BringgSdkViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val taskViewHolderClickListener: ClickListener
) : ListAdapter<Task, TaskViewHolder>(object : ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.getId() == newItem.getId()

    override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem.equals(newItem)
}) {

    var tracker: SelectionTracker<Long>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            ListItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            viewModel,
            lifecycleOwner,
            taskViewHolderClickListener
        )
    }

    override fun onViewDetachedFromWindow(holder: TaskViewHolder) {
        super.onViewDetachedFromWindow(holder)
        removeExtrasObserver(holder)
    }

    override fun onViewRecycled(holder: TaskViewHolder) {
        super.onViewRecycled(holder)
        removeExtrasObserver(holder)
    }

    private fun removeExtrasObserver(holder: TaskViewHolder) {
        val taskId = holder.getItemDetails().selectionKey
        taskId?.let {
            viewModel.data.extras.taskExtras(taskId).removeObservers(lifecycleOwner)
        }
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        tracker?.let {
            holder.bind(getItem(position), it.isSelected(holder.getItemDetails().selectionKey))
        }
    }

    fun getItemAt(position: Int) = getItem(position).getId()
    fun getPosition(id: Long) = currentList.indexOfFirst { it.getId() == id }
}
