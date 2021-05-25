package com.bringg.android.example.driversdk.clusters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import com.bringg.android.example.driversdk.R
import driver_sdk.models.Cluster

class ClusterListAdapter(
    private val clusterViewHolderClickListener: ClusterViewHolder.ClickListener
) : ListAdapter<Cluster, ClusterViewHolder>(object : ItemCallback<Cluster>() {
    override fun areItemsTheSame(oldItem: Cluster, newItem: Cluster) = oldItem.wayPoints.map { it.id } == newItem.wayPoints.map { it.id }

    override fun areContentsTheSame(oldItem: Cluster, newItem: Cluster) = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClusterViewHolder {
        return ClusterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_cluster, parent, false), clusterViewHolderClickListener)
    }

    override fun onBindViewHolder(holder: ClusterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}