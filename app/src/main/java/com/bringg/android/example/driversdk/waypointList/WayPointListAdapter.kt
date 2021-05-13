package com.bringg.android.example.driversdk.waypointList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R

class WayPointListAdapter(private val waypoints: List<driver_sdk.models.tasks.Waypoint>) : RecyclerView.Adapter<WayPointListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WayPointListViewHolder {
        return WayPointListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_waypoint, parent, false))
    }

    override fun onBindViewHolder(holder: WayPointListViewHolder, position: Int) {
        holder.bind(waypoints[position])
    }

    override fun getItemCount(): Int {
        return waypoints.size
    }
}