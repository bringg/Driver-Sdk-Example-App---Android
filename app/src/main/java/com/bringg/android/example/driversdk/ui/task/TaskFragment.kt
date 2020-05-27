package com.bringg.android.example.driversdk.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bringg.android.example.driversdk.R
import com.google.android.material.tabs.TabLayoutMediator
import driver_sdk.DriverSdkProvider
import kotlinx.android.synthetic.main.task_fragment.*

class TaskFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.task_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val taskLiveData = DriverSdkProvider.driverSdk().data.task(requireArguments().getLong("task_id"))
        val adapter = WaypointAdapter(this, taskLiveData)
        vp_task_waypoints.adapter = adapter
        TabLayoutMediator(tabLayout, vp_task_waypoints) { tab, position ->
            tab.text = if(taskLiveData.value?.currentWayPointIndex == position) "Current Destination" else "Destination ${position + 1}"
        }.attach()
    }
}
