package com.bringg.android.example.driversdk.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.authentication.AuthenticatedFragment

class WaypointFragment : AuthenticatedFragment() {

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
        viewModel.data.waypoint(waypointId).observe(viewLifecycleOwner, WaypointViewObserver(viewModel, waypointId, view, findNavController()))
    }
}
