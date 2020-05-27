package com.bringg.android.example.driversdk.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bringg.android.example.driversdk.R
import driver_sdk.DriverSdkProvider

class WaypointFragment : Fragment() {

    companion object {
        fun newInstance(taskId: Long, waypointId: Long): WaypointFragment {
            val fragment = WaypointFragment()
            val args = bundleOf("task_id" to taskId, "waypoint_id" to waypointId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.waypoint_fragment, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val waypointId = requireArguments().getLong("waypoint_id")
        val driverSdk = DriverSdkProvider.driverSdk()
        driverSdk.data.waypoint(waypointId).observe(viewLifecycleOwner, WaypointViewObserver(waypointId, view, findNavController()))
    }
}
