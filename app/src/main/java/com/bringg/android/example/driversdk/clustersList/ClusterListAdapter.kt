package com.bringg.android.example.driversdk.clustersList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import driver_sdk.models.Cluster

class ClusterListAdapter(fragment: Fragment, private val clusterList: LiveData<List<Cluster>>, private val clusterViewHolderClickListener: ClusterViewHolder.ClickListener) : RecyclerView.Adapter<ClusterViewHolder>() {

    init {
        clusterList.observe(fragment, Observer { notifyDataSetChanged() })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClusterViewHolder {
        return ClusterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_cluster, parent, false), clusterViewHolderClickListener)
    }

    override fun getItemCount(): Int {
        val clusterList = clusterList.value ?: return 0
        return clusterList.size
    }

    override fun onBindViewHolder(holder: ClusterViewHolder, position: Int) {
        holder.bind(clusterList.value!![position])
    }
}