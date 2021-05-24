package com.bringg.android.example.driversdk.clustersList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import driver_sdk.models.Waypoint

class ClusterViewModel : ViewModel() {

    private val _waypoints = MutableLiveData<List<Waypoint>>()
    val waypoints: LiveData<List<Waypoint>> = _waypoints

    fun onWaypointsChanged(waypointsList: List<Waypoint>) {
        _waypoints.value = waypointsList
    }
}