package com.bringg.android.example.driversdk.ui.taskList

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.clustersList.ClusterListAdapter
import com.bringg.android.example.driversdk.clustersList.ClusterViewHolder
import com.bringg.android.example.driversdk.homelist.HomeListAdapter
import com.bringg.android.example.driversdk.tasklist.TaskListAdapter
import com.bringg.android.example.driversdk.tasklist.TaskViewHolder
import com.bringg.android.example.driversdk.ui.AuthenticatedFragment
import driver_sdk.DriverSdkProvider
import driver_sdk.content.ResultCallback
import driver_sdk.driver.model.result.ShiftEndResult
import driver_sdk.driver.model.result.ShiftStartResult
import driver_sdk.models.Task
import driver_sdk.models.tasks.ClusterArea
import kotlinx.android.synthetic.main.task_list_fragment.*

class TaskListFragment : AuthenticatedFragment() {

    private val TAG = "TaskListFragment"
    private val START_SHIFT_WITH_LOCATION_PERMISSION_REQUEST_CODE = 5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.task_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val driverSdk = DriverSdkProvider.driverSdk()
        task_list_shift_state_button.setOnClickListener {
            if (driverSdk.isOnShift()) {
                driverSdk.shift.endShift(object : ResultCallback<ShiftEndResult> {
                    override fun onResult(result: ShiftEndResult) {
                        if (result.success) {
                            Log.i(TAG, "user is offline, DriverSdk stopped all background processes, DriverSdkProvider.driverSdk.data.online will post FALSE")
                        } else {
                            Log.i(TAG, "end shift request failed, error=${result.error}")
                        }
                    }
                })
            } else {
                if (PermissionChecker.checkSelfPermission(view.context, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), START_SHIFT_WITH_LOCATION_PERMISSION_REQUEST_CODE)
                    return@setOnClickListener
                }
                startShift()
            }
        }

        driverSdk.data.online.observe(viewLifecycleOwner, Observer<Boolean> { t ->
            t?.let {
                if (t) {
                    task_list_shift_state_button.text = task_list_shift_state_button.resources.getString(R.string.end_shift)
                } else {
                    task_list_shift_state_button.text = task_list_shift_state_button.resources.getString(R.string.start_shift)
                }
            }
        })

        home_state_recycler.adapter = HomeListAdapter(this, driverSdk.data.homeMap)

        val taskList = driverSdk.data.taskList
        val clusters = driverSdk.data.clustersList

        task_list_swipe_to_refresh.setOnRefreshListener {
            Log.i(TAG, "onRefresh called from SwipeRefreshLayout")
            taskList.observe(viewLifecycleOwner, object : Observer<List<Task>> {
                override fun onChanged(t: List<Task>?) {
                    task_list_swipe_to_refresh.isRefreshing = false
                    taskList.removeObserver(this)
                }
            })
            DriverSdkProvider.driverSdk().data.refreshTaskList()
        }

        val adapter = TaskListAdapter(this, taskList, object : TaskViewHolder.ClickListener {
            override fun onTaskItemClick(task: Task) {
                findNavController().navigate(TaskListFragmentDirections.actionTaskListToTaskFragment(task.getId()))
            }
        })
        rv_task_list.adapter = adapter

        val clustersAdapter = ClusterListAdapter(this, clusters, object : ClusterViewHolder.ClickListener {
            override fun onClusterItemClick(cluster: ClusterArea) {
                findNavController().navigate(TaskListFragmentDirections.actionTaskListToClusterFragment(cluster.wayPoints))
            }
        })
        rv_cluster_list.adapter = clustersAdapter
    }

    private fun startShift() {
        DriverSdkProvider.driverSdk().shift.startShift(object : ResultCallback<ShiftStartResult> {
            override fun onResult(result: ShiftStartResult) {
                if (result.success) {
                    Log.i(TAG, "user is online, DriverSdk is working in the background, DriverSdkProvider.driverSdk.data.online will post TRUE")
                } else {
                    Log.i(TAG, "start shift request failed, error=${result.error}")
                }
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (START_SHIFT_WITH_LOCATION_PERMISSION_REQUEST_CODE == requestCode && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            startShift()
        }
    }
}
