package com.bringg.android.example.driversdk.clusters.waypointList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import java.util.LinkedList

class WayPointListAdapter(fragment: Fragment, private val waypoints: LiveData<List<driver_sdk.models.Waypoint>>) : RecyclerView.Adapter<WayPointListViewHolder>() {
    private val wpList: MutableList<driver_sdk.models.Waypoint> = LinkedList()

    init {
        waypoints.observe(fragment, {
            wpList.clear()
            wpList.addAll(it.toList())
            notifyDataSetChanged()
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WayPointListViewHolder {
        return WayPointListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_waypoint, parent, false))
    }

    override fun onBindViewHolder(holder: WayPointListViewHolder, position: Int) {
        holder.bind(wpList[position])
    }

    override fun getItemCount(): Int {
        return wpList.size
    }
}