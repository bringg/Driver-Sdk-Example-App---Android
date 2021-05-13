package com.bringg.android.example.driversdk.ui.cluster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.waypointList.WayPointListAdapter
import kotlinx.android.synthetic.main.fragment_cluster.*

class ClusterFragment : Fragment() {

    private val args: ClusterFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cluster, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_waypoint_list.adapter = WayPointListAdapter(args.waypoint.asList())
    }
}