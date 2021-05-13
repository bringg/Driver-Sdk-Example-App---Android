package com.bringg.android.example.driversdk.waypointList

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.util.TaskStatusMap
import driver_sdk.models.Waypoint

class WayPointListViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val customerName: TextView = itemView.findViewById(R.id.customer_name)
    private val wayPointTitle: TextView = itemView.findViewById(R.id.way_point_title)
    private val taskId: TextView = itemView.findViewById(R.id.task_id)
    private val wayPointStatus: TextView = itemView.findViewById(R.id.way_point_status)

    fun bind(waypoint: Waypoint) {
        customerName.text = waypoint.customerName

        if (!waypoint.title.isNullOrEmpty()) {
            wayPointTitle.text = waypoint.title
        } else if (!waypoint.name.isNullOrEmpty()) {
            wayPointTitle.text = waypoint.name
        } else {
            wayPointTitle.visibility = View.GONE
        }

        taskId.text = String.format("task id: ${waypoint.taskId}")
        wayPointStatus.text = String.format("Waypoint status: ${TaskStatusMap.getUserStatus(waypoint.status)}")
    }
}