package com.bringg.android.example.driversdk.homelist

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import driver_sdk.location.home.HomeState
import driver_sdk.models.Home

class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(home: Home, homeState: HomeState?) {
        itemView.findViewById<TextView>(R.id.home_state_text).text = "homeId ${home.id} state is $homeState"
    }

}
