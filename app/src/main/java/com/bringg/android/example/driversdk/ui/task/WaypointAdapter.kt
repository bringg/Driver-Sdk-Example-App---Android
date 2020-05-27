package com.bringg.android.example.driversdk.ui.task

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import driver_sdk.models.Task

class WaypointAdapter(fragment: Fragment, private val task: LiveData<Task?>) :
    FragmentStateAdapter(fragment) {

    init {
        task.observe(fragment, Observer { notifyDataSetChanged() })
    }

    override fun getItemCount(): Int {
        return task.value?.wayPoints?.size ?: 0
    }

    override fun createFragment(position: Int): Fragment {
        val waypoint = task.value?.wayPoints?.get(position) ?: return Fragment()
        return WaypointFragment.newInstance(waypoint.taskId, waypoint.id)
    }
}