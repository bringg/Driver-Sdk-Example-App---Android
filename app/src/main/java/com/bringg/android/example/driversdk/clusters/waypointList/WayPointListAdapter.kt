package com.bringg.android.example.driversdk.clusters.waypointList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import com.bringg.android.example.driversdk.R
import driver_sdk.models.Waypoint

class WayPointListAdapter : ListAdapter<Waypoint, WayPointListViewHolder>(object : ItemCallback<Waypoint>() {
    override fun areItemsTheSame(oldItem: Waypoint, newItem: Waypoint) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Waypoint, newItem: Waypoint) = oldItem.equals(newItem)
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WayPointListViewHolder {
        return WayPointListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_waypoint, parent, false))
    }

    override fun onBindViewHolder(holder: WayPointListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}