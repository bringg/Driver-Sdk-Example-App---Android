package com.bringg.android.example.driversdk.ui.task

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.ui.AuthenticatedFragment
import com.google.android.material.tabs.TabLayoutMediator
import driver_sdk.DriverSdkProvider
import driver_sdk.models.Waypoint
import driver_sdk.models.configuration.TaskActionItem
import kotlinx.android.synthetic.main.task_fragment.*


class TaskFragment : AuthenticatedFragment() {

    private val args: TaskFragmentArgs by navArgs()
    private val driverSdk = DriverSdkProvider.driverSdk()

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

        val taskLiveData = driverSdk.data.task(args.taskId)
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
            R.id.task_actions -> {
                showTaskActionsDialog()
                return true
            }
        }
        return false
    }

    private fun showCancelDialog() {
        findNavController().navigate(TaskFragmentDirections.actionTaskFragmentToDialogCancelTask(args.taskId))
    }

    private fun showTaskActionsDialog() {
        val currWayPointId = driverSdk.data.task(args.taskId).value?.currentWayPointId
        if (currWayPointId != null) {
            val mandatoryActions = driverSdk.data.waypoint(currWayPointId).value?.remainingMandatoryActions // TODO
            val actionsArray = mandatoryActions?.toTypedArray() ?: arrayOf<TaskActionItem>()
            findNavController().navigate(
                TaskFragmentDirections.actionTaskFragmentToDialogActions(
                    actionsArray
                )
            )
        }
    }
}
