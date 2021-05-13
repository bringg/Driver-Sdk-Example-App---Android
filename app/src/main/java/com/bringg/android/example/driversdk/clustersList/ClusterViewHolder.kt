package com.bringg.android.example.driversdk.clustersList

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import driver_sdk.models.tasks.ClusterArea

class ClusterViewHolder(
    itemView: View,
    clickListener: ClusterViewHolder.ClickListener
) : RecyclerView.ViewHolder(itemView) {

    interface ClickListener {
        fun onClusterItemClick(cluster: ClusterArea)
    }

    private val TAG = "ClusterViewHolder"

    private val title: TextView
    private val typeLabel: TextView
    private val totalWayPoints: TextView
    private val address: TextView
    private val customerAddressName: TextView

    init {
        val view = itemView
        itemView.setOnClickListener { clickListener.onClusterItemClick(it.tag as ClusterArea) }
        title = view.findViewById(R.id.task_description)
        typeLabel = view.findViewById(R.id.task_special_status_second_label)
        totalWayPoints = view.findViewById(R.id.total_way_points)
        address = view.findViewById(R.id.way_point_address)
        customerAddressName = view.findViewById(R.id.customer_address_name)
    }

    fun bind(clusterArea: ClusterArea) {
        itemView.tag = clusterArea

        // set the title, address and scheduled_at
        title.text = if (clusterArea.isPickup) "Pickup Area" else "Drop-off Area"

        totalWayPoints.text = String.format("Total way points: ${clusterArea.wayPoints.size}")

        setAddress(clusterArea.address)
        setCustomerAddress(clusterArea)

        updateLabelTaskType(clusterArea.isPickup)
    }


    private fun updateLabelTaskType(isPickup: Boolean) {
        var labelBackgroundResource = 0
        var labelStringResource = 0

        if (isPickup) {
            labelStringResource = R.string.inventory_collect_label
            labelBackgroundResource = R.drawable.task_list_item_pickup_bg
        } else {
            labelStringResource = R.string.inventory_deliver_label
            labelBackgroundResource = R.drawable.task_list_item_dropoff_bg
        }
        typeLabel.setText(labelStringResource)
        typeLabel.setBackgroundResource(labelBackgroundResource)
    }

    private fun setCustomerAddress(clusterArea: ClusterArea) {
        val waypoint = clusterArea.wayPoints[0]
        if (waypoint != null) {
            if (waypoint.locationName.isBlank()) {
                customerAddressName.visibility = View.GONE
            } else {
                customerAddressName.visibility = View.VISIBLE
                customerAddressName.text = waypoint.locationName
            }
        } else {
            customerAddressName.visibility = View.GONE
        }
    }

    private fun setAddress(address: String) {
        this.address.text = address
    }
}