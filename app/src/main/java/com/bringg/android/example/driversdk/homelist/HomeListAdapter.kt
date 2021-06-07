package com.bringg.android.example.driversdk.homelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import com.bringg.android.example.driversdk.R
import driver_sdk.location.home.HomeState
import driver_sdk.models.Home

class HomeListAdapter : ListAdapter<Pair<Home, HomeState>, HomeViewHolder>(object : ItemCallback<Pair<Home, HomeState>>() {
    override fun areItemsTheSame(oldItem: Pair<Home, HomeState>, newItem: Pair<Home, HomeState>) =
        oldItem.first.id == newItem.first.id

    override fun areContentsTheSame(oldItem: Pair<Home, HomeState>, newItem: Pair<Home, HomeState>) =
        oldItem.second == newItem.second
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_home_state, parent, false))
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val homeStatePair = getItem(position)
        holder.bind(homeStatePair.first, homeStatePair.second)
    }
}
