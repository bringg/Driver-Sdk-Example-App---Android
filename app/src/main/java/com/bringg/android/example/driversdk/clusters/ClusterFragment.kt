package com.bringg.android.example.driversdk.clusters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bringg.android.example.driversdk.BringgSdkViewModel
import com.bringg.android.example.driversdk.clusters.waypointList.WayPointListAdapter
import com.bringg.android.example.driversdk.databinding.FragmentClusterBinding

class ClusterFragment : Fragment() {

    private val clusterViewModel: ClusterViewModel by activityViewModels()
    private val bringgSdkViewModel: BringgSdkViewModel by activityViewModels()

    private var _binding: FragmentClusterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClusterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val wayPointListAdapter = WayPointListAdapter()
        clusterViewModel.waypoints.observe(viewLifecycleOwner) {
            Log.i("clusters", "updated list=$it")
            wayPointListAdapter.submitList(it)
            val waypointIds = it.map { waypoint -> waypoint.id }
            binding.btnStart.setOnClickListener { bringgSdkViewModel.startWaypoints(waypointIds) }
            binding.btnArrive.setOnClickListener { bringgSdkViewModel.arriveWaypoints(waypointIds) }
            binding.btnLeave.setOnClickListener { bringgSdkViewModel.completeWaypoints(waypointIds) }
        }
        binding.waypointList.adapter = wayPointListAdapter
    }
}