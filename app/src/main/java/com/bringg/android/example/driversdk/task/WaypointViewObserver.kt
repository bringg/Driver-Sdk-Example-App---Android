package com.bringg.android.example.driversdk.task

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.bringg.android.example.driversdk.BringgSdkViewModel
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.task.ui.view.InventoryListPresenter
import driver_sdk.content.ResultCallback
import driver_sdk.driver.model.result.WaypointLeaveResult
import driver_sdk.models.Inventory
import driver_sdk.models.Waypoint

class WaypointViewObserver(private val viewModel: BringgSdkViewModel, private val waypointId: Long, view: View, private val navController: NavController) : Observer<Waypoint?>, InventoryListPresenter {

    private val TAG = "WaypointViewObserver"
    private val btnNextAction = view.findViewById<TextView>(R.id.btn_waypoint_progress)
    private val btnReject = view.findViewById<TextView>(R.id.btn_waypoint_reject)

    override fun onChanged(waypoint: Waypoint?) {
        btnReject.visibility = View.GONE
        if (waypoint == null) {
            btnNextAction.isEnabled = true
            btnNextAction.text = "This waypoint was removed"
            btnNextAction.setOnClickListener { navController.navigateUp() }
        } else {
            val task = viewModel.data.task(waypoint.taskId).value
            if (waypoint.isDone) {
                btnNextAction.isEnabled = true
                btnNextAction.text = "Done"
                btnNextAction.setOnClickListener { navController.navigateUp() }
            } else if (!task!!.isAccepted) {
                btnReject.setOnClickListener {
                    viewModel.rejectTask(task.getId())
                }
                btnReject.visibility = View.VISIBLE
                btnNextAction.isEnabled = true
                btnNextAction.text = "Accept Order"
                btnNextAction.setOnClickListener {
                    viewModel.acceptTask(task.getId())
                }
            } else {
                btnNextAction.isEnabled = true
                if (waypoint.isCheckedIn) {
                    btnNextAction.text = "Order Collected"
                    btnNextAction.setOnClickListener {
                        viewModel.leaveWayPoint(waypoint.id, object : ResultCallback<WaypointLeaveResult> {
                            override fun onResult(result: WaypointLeaveResult) {
                                Log.i(TAG, "leave waypoint result=$result")
                                if (result.requiredActions.isNotEmpty()) {
                                    navController.navigate(TaskFragmentDirections.actionTaskFragmentToDialogActions(result.requiredActions.toTypedArray()))
                                }
                            }
                        })
                    }
                } else if (waypoint.isStarted) {
                    btnNextAction.text = "Arrived"
                    btnNextAction.setOnClickListener {
                        viewModel.arriveToWayPoint(waypoint.id)
                    }
                } else {
                    btnNextAction.text = "Start"
                    btnNextAction.setOnClickListener {
                        viewModel.startTask(task.getId())
                    }
                }
            }
        }

        if (!viewModel.isOnShift()) {
            btnNextAction.isEnabled = true
            btnNextAction.text = btnNextAction.resources.getString(R.string.start_shift)
            btnNextAction.setOnClickListener {
                viewModel.startShift()
            }
        }
    }

    override fun showInventoryList(inventory: Inventory) {
        navController.navigate(TaskFragmentDirections.actionTaskFragmentToInventoryFragment(waypointId))
    }
}
