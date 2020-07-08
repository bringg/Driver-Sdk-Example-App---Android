package com.bringg.android.example.driversdk.homelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import driver_sdk.location.home.HomeState
import driver_sdk.models.Home
import java.util.*

class HomeListAdapter(fragment: Fragment, private val homeMap: LiveData<Map<Home, HomeState>>) : RecyclerView.Adapter<HomeViewHolder>() {

    private val homeList: MutableList<Pair<Home, HomeState>> = LinkedList()

    init {
        homeMap.observe(fragment, Observer {
            homeList.clear()
            homeList.addAll(it.filter { it.key != null && it.value != null }.toList())
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
