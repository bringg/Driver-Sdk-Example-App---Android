package com.bringg.android.example.driversdk.ui.task

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.ui.task.ui.view.InventoryListPresenter
import com.bringg.android.example.driversdk.ui.task.ui.view.WaypointView
import driver_sdk.DriverSdkProvider
import driver_sdk.content.ResultCallback
import driver_sdk.driver.model.result.ShiftStartResult
import driver_sdk.driver.model.result.TaskAcceptResult
import driver_sdk.driver.model.result.TaskStartResult
import driver_sdk.driver.model.result.WaypointArriveResult
import driver_sdk.driver.model.result.WaypointLeaveResult
import driver_sdk.models.Inventory
import driver_sdk.models.Waypoint

class WaypointViewObserver(private val waypointId: Long, view: View, private val navController: NavController) : Observer<Waypoint?>, InventoryListPresenter {

    private val TAG = "WaypointFragment"
    private val btnNextAction = view.findViewById<TextView>(R.id.btn_waypoint_progress)
    private val waypointView = view.findViewById<WaypointView>(R.id.waypoint_view)
    private val driverSdk = DriverSdkProvider.driverSdk()

    override fun onChanged(waypoint: Waypoint?) {
        if (waypoint == null) {
            waypointView.refresh(null, null, this)
            btnNextAction.isEnabled = true
            btnNextAction.text = "This waypoint was removed"
            btnNextAction.setOnClickListener { navController.navigateUp() }
        } else {
            val task = driverSdk.data.task(waypoint.taskId).value
            waypointView.refresh(task, waypoint, this)
            if (!task!!.isAccepted) {
                btnNextAction.isEnabled = true
                btnNextAction.text = "Accept Order"
                btnNextAction.setOnClickListener {
                    driverSdk.task.acceptTask(task.getId(), object : ResultCallback<TaskAcceptResult> {
                        override fun onResult(result: TaskAcceptResult) {
                            if (result.success) {
                                Log.i(TAG, "task was successfully accepted, LiveData event will be posted, result=$result")
                            } else {
                                Log.i(TAG, "accepting the task failed, error=${result.error}")
                            }
                        }
                    })
                }
            } else if (waypoint.isDone) {
                btnNextAction.isEnabled = true
                btnNextAction.text = "Done"
                btnNextAction.setOnClickListener { navController.navigateUp() }
            } else {
                btnNextAction.isEnabled = true
                if (waypoint.isCheckedIn) {
                    btnNextAction.text = "Order Collected"
                    btnNextAction.setOnClickListener {
                        driverSdk.task.leaveWayPoint(waypoint.id, object : ResultCallback<WaypointLeaveResult> {
                            override fun onResult(result: WaypointLeaveResult) {
                                Log.i(TAG, "leave waypoint result=$result")
                            }
                        })
                    }
                } else if (waypoint.isStarted) {
                    btnNextAction.text = "Arrived"
                    btnNextAction.setOnClickListener {
                        driverSdk.task.arriveToWayPoint(waypoint.id, object : ResultCallback<WaypointArriveResult> {
                            override fun onResult(result: WaypointArriveResult) {
                                Log.i(TAG, "arrive waypoint result=$result")
                            }
                        })
                    }
                } else {
                    btnNextAction.text = "Start"
                    btnNextAction.setOnClickListener {
                        driverSdk.task.startTask(task.getId(), object : ResultCallback<TaskStartResult> {
                            override fun onResult(result: TaskStartResult) {
                                if (result.success) {
                                    Log.i(TAG, "task was successfully started, LiveData event will be posted, result=$result")
                                } else {
                                    Log.i(TAG, "starting the task failed, error=${result.error}")
                                }
                            }
                        })
                    }
                }
            }
        }

        if (!driverSdk.isOnShift()) {
            btnNextAction.isEnabled = true
            btnNextAction.text = btnNextAction.resources.getString(R.string.start_shift)
            btnNextAction.setOnClickListener {
                driverSdk.shift.startShift(object : ResultCallback<ShiftStartResult> {
                    override fun onResult(result: ShiftStartResult) {
                        if (result.success) {
                            Log.i(TAG, "user is online, DriverSdk is working in the background, DriverSdkProvider.driverSdk.data.online will post TRUE")
                        } else {
                            Log.i(TAG, "start shift request failed, error=${result.error}")
                        }
                    }
                })
            }
        }
    }

    override fun showInventoryList(inventory: Inventory) {
        val args = bundleOf("waypoint_id" to waypointId)
        navController.navigate(R.id.action_task_fragment_to_inventory_fragment, args)
    }
}
