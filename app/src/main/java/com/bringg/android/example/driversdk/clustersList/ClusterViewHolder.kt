package com.bringg.android.example.driversdk.clustersList

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import driver_sdk.models.Cluster
import driver_sdk.models.tasks.ClusterArea

class ClusterViewHolder(
    itemView: View,
    clickListener: ClickListener
) : RecyclerView.ViewHolder(itemView) {

    interface ClickListener {
        fun onClusterItemClick(cluster: Cluster)
    }

    private val TAG = "ClusterViewHolder"

    private val title: TextView
    private val totalWayPoints: TextView
    private val address: TextView

    init {
        val view = itemView
        itemView.setOnClickListener { clickListener.onClusterItemClick(it.tag as ClusterArea) }
        title = view.findViewById(R.id.task_description)
        totalWayPoints = view.findViewById(R.id.total_way_points)
        address = view.findViewById(R.id.way_point_address)
    }

    fun bind(clusterArea: Cluster) {
        itemView.tag = clusterArea

        title.setText(if (clusterArea.isPickup) R.string.pickup_area else R.string.dropoff_area)
        totalWayPoints.text = String.format("Total way points: ${clusterArea.wayPoints.size}")
        address.text = clusterArea.wayPoints[0].address
    }
}