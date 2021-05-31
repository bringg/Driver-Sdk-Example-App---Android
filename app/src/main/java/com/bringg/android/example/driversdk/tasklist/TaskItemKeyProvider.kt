package com.bringg.android.example.driversdk.tasklist

import androidx.recyclerview.selection.ItemKeyProvider

class TaskItemKeyProvider(private val adapter: TaskListAdapter) :
    ItemKeyProvider<Long>(SCOPE_CACHED) {
    override fun getKey(position: Int): Long {
        return adapter.getItemAt(position)
    }

    override fun getPosition(taskId: Long): Int {
        return adapter.getPosition(taskId)
    }
}