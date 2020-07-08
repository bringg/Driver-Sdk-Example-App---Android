package com.bringg.android.example.driversdk.ui.taskList

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.homelist.HomeListAdapter
import com.bringg.android.example.driversdk.tasklist.TaskListAdapter
import com.bringg.android.example.driversdk.tasklist.TaskViewHolder
import driver_sdk.DriverSdkProvider
import driver_sdk.content.ResultCallback
import driver_sdk.driver.model.result.ShiftEndResult
import driver_sdk.driver.model.result.ShiftStartResult
import driver_sdk.models.Task
import kotlinx.android.synthetic.main.task_list_fragment.*

class TaskListFragment : Fragment() {

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

        val adapter = TaskListAdapter(this, driverSdk.data.taskList, object : TaskViewHolder.ClickListener {
            override fun onTaskItemClick(task: Task) {
                val args = bundleOf("task_id" to task.getId())
                findNavController().navigate(R.id.action_task_list_to_task_fragment, args)
            }
        })
        rv_task_list.adapter = adapter
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
