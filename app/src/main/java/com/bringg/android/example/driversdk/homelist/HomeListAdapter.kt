package com.bringg.android.example.driversdk.homelist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import driver_sdk.location.home.HomeState
import driver_sdk.models.Home
import java.util.LinkedList

class HomeListAdapter(fragment: Fragment, private val homeMap: LiveData<Map<Home, HomeState>>) : RecyclerView.Adapter<HomeViewHolder>() {

    private val homeList: MutableList<Pair<Home, HomeState>> = LinkedList()

    init {
        homeMap.observe(fragment, {
            Log.i("homes changed", "home states:")
            it.forEach { entry ->
                Log.i("homes changed", "homeId=${entry.key.id}, state=${entry.value}")
            }
            homeList.clear()
            homeList.addAll(it.toList())
            notifyDataSetChanged()
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_home_state, parent, false))
    }

    override fun getItemCount(): Int {
        return homeList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val home = homeList[position]
        holder.bind(home.first, home.second)
    }
}
