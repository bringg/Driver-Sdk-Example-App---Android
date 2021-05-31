package com.bringg.android.example.driversdk.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.authentication.AuthenticatedFragment
import com.bringg.android.example.driversdk.task.ui.view.WaypointView

class WaypointFragment : AuthenticatedFragment() {

    companion object {
        fun newInstance(taskId: Long, waypointId: Long): WaypointFragment {
            val fragment = WaypointFragment()
            val args = bundleOf("task_id" to taskId, "waypoint_id" to waypointId)
            fragment.arguments = args
            return fragment
        }
    }

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.waypoint_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.edit_waypoint) {
            val args = requireArguments()
            findNavController().navigate(TaskFragmentDirections.actionTaskFragmentToEditWaypointFragment(args.getLong("task_id"), args.getLong("waypoint_id")))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.waypoint_fragment, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val waypointId = requireArguments().getLong("waypoint_id")
        val waypointView = view.findViewById<WaypointView>(R.id.waypoint_view)
        val waypointNextActionButtonObserver = WaypointViewObserver(viewModel, waypointId, view, findNavController())
        viewModel.data.waypoint(waypointId).observe(viewLifecycleOwner) { waypoint ->
            if (waypoint == null) {
                waypointView.refresh(null, null, waypointNextActionButtonObserver)
            } else {
                val task = viewModel.data.task(waypoint.taskId).value
                waypointView.refresh(task, waypoint, waypointNextActionButtonObserver)
            }
            waypointNextActionButtonObserver.onChanged(waypoint)
        }
        viewModel.data.extras.waypointExtras(waypointId).observe(viewLifecycleOwner) {
            waypointView.setExtras(it)
        }
    }
}
