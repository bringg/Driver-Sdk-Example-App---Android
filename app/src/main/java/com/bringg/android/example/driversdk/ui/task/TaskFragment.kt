package com.bringg.android.example.driversdk.ui.task

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bringg.android.example.driversdk.R
import com.google.android.material.tabs.TabLayoutMediator
import driver_sdk.DriverSdkProvider
import driver_sdk.models.Waypoint
import kotlinx.android.synthetic.main.task_fragment.*


class TaskFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.task_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val driverSdk = DriverSdkProvider.driverSdk()
        val taskLiveData = driverSdk.data.task(taskId())
        val findNavController = findNavController()
        taskLiveData.observe(viewLifecycleOwner, Observer { task ->
            Log.v("Task updated", "task=$task")
            if (task == null || !task.isAvailable) {
                findNavController.navigateUp()
            } else {
                task.wayPoints.forEachIndexed { index: Int, wp: Waypoint ->
                    if (!wp.isDone) {
                        vp_task_waypoints.currentItem = index
                        return@Observer
                    }
                }
                findNavController.navigateUp()
            }
        })
        val adapter = WaypointAdapter(this, taskLiveData)
        vp_task_waypoints.adapter = adapter
        TabLayoutMediator(tabLayout, vp_task_waypoints) { tab, position ->
            tab.text = if (taskLiveData.value?.currentWayPointIndex == position) "Current Destination" else "Destination ${position + 1}"
        }.attach()
    }

    private fun taskId() = requireArguments().getLong("task_id")

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.task_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancel_task -> {
                showCancelDialog()
                return true
            }
        }
        return false
    }

    private fun showCancelDialog() {
        val taskId = taskId()
        val reasons = DriverSdkProvider.driverSdk().data.task(taskId).value?.cancelReasons ?: emptyList()
        val args = bundleOf("reasons" to ArrayList(reasons), "task_id" to taskId)
        findNavController().navigate(R.id.dialog_cancel_task, args)
    }
}
