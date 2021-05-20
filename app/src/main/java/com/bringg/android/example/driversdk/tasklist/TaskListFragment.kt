package com.bringg.android.example.driversdk.tasklist

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.PermissionChecker
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.MutableSelection
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker.Builder
import androidx.recyclerview.selection.SelectionTracker.SelectionObserver
import androidx.recyclerview.selection.StorageStrategy
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.R.plurals
import com.bringg.android.example.driversdk.databinding.TaskListFragmentBinding
import com.bringg.android.example.driversdk.homelist.HomeListAdapter
import com.bringg.android.example.driversdk.tasklist.TaskViewHolder.ClickListener
import com.bringg.android.example.driversdk.ui.AuthenticatedFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.snackbar.Snackbar
import driver_sdk.DriverSdkProvider
import driver_sdk.content.ResultCallback
import driver_sdk.driver.model.result.CreateGroupTaskResult
import driver_sdk.driver.model.result.ShiftEndResult
import driver_sdk.driver.model.result.ShiftStartResult
import driver_sdk.driver.model.result.UnGroupTaskResult
import driver_sdk.models.Task
import driver_sdk.tasks.TaskCancelResult

class TaskListFragment : AuthenticatedFragment() {

    private val TAG = "TaskListFragment"
    private val START_SHIFT_WITH_LOCATION_PERMISSION_REQUEST_CODE = 5

    private var _binding: TaskListFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TaskListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeHomeStates()
        observeTaskList()
        initBottomSheet()

        binding.taskListSwipeToRefresh.setOnRefreshListener {
            DriverSdkProvider.driverSdk().data.refreshTaskList()
        }
    }

    private fun observeHomeStates() {
        val homeListAdapter = HomeListAdapter()
        binding.homeStateRecycler.adapter = homeListAdapter
        DriverSdkProvider.driverSdk().data.homeMap.observe(viewLifecycleOwner) {
            Log.i("homes changed", "home states:")
            homeListAdapter.submitList(it.toList())
        }
    }

    private fun observeTaskList() {
        val taskList = DriverSdkProvider.driverSdk().data.taskList
        val adapter = TaskListAdapter(object : ClickListener {
            override fun onTaskItemClick(task: Task) {
                findNavController().navigate(TaskListFragmentDirections.actionTaskListToTaskFragment(task.getId()))
            }
        })
        taskList.observe(viewLifecycleOwner) {
            binding.taskListSwipeToRefresh.isRefreshing = false
            adapter.submitList(it)
        }
        binding.rvTaskList.adapter = adapter
        initSelectionTracker(adapter)
    }

    private fun initSelectionTracker(adapter: TaskListAdapter) = with(binding.rvTaskList) {
        val tracker = Builder(
            "taskListSelection",
            this,
            TaskItemKeyProvider(adapter),
            TaskItemDetailsLookup(this),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        tracker.addObserver(
            object : SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    onSelectedOrdersChanged(tracker.selection)
                }
            })
        adapter.tracker = tracker
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.taskListFragmentBottomSheet.root)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    binding.homeStateCard.visibility = View.VISIBLE
                else
                    binding.homeStateCard.visibility = View.GONE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
        onSelectedOrdersChanged(MutableSelection())
        initShiftButton()
    }

    private fun initShiftButton() = with(binding.taskListFragmentBottomSheet.taskListShiftStateButton) {
        val driverSdk = DriverSdkProvider.driverSdk()
        driverSdk.data.online.observe(viewLifecycleOwner, { isUserOnline ->
            setText(if (isUserOnline) R.string.end_shift else R.string.start_shift)
            setOnClickListener {
                if (isUserOnline) {
                    driverSdk.shift.endShift(object : ResultCallback<ShiftEndResult> {
                        override fun onResult(result: ShiftEndResult) {
                            if (result.success) {
                                Log.i(TAG, "user is offline, DriverSdk stopped all background processes, DriverSdkProvider.driverSdk.data.online will post FALSE")
                            } else {
                                Log.i(TAG, "end shift request failed, error=${result.error}")
                            }
                        }
                    })
                } else {
                    startShift(context)
                }
            }
        })
    }

    private fun onSelectedOrdersChanged(selectedOrderIds: Selection<Long>) = with(binding.taskListFragmentBottomSheet) {
        Log.i(TAG, "selected items=$selectedOrderIds")
        if (selectedOrderIds.isEmpty) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        refreshCancelButton(selectedOrderIds)
        refreshGroupButton(selectedOrderIds)
        refreshUnGroupButton(selectedOrderIds)
    }

    private fun refreshCancelButton(selectedOrderIds: Selection<Long>) = with(binding.taskListFragmentBottomSheet.btnCancelOrder) {
        isEnabled = !selectedOrderIds.isEmpty
        text = resources.getQuantityString(plurals.cancel_task_button, selectedOrderIds.size(), selectedOrderIds.size())
        setOnClickListener {
            selectedOrderIds.forEach {
                DriverSdkProvider.driverSdk().task.cancelTask(it, "reason to cancel", "custom reason", object : ResultCallback<TaskCancelResult> {
                    override fun onResult(result: TaskCancelResult) {
                        Log.i(TAG, "cancel result=$result")
                    }
                })
            }
        }
    }

    private fun refreshGroupButton(selectedOrderIds: Selection<Long>) = with(binding.taskListFragmentBottomSheet.btnGroupOrders) {
        isEnabled = selectedOrderIds.size() > 1
        text = resources.getQuantityString(plurals.merge_orders_button, selectedOrderIds.size(), selectedOrderIds.size())
        if (isEnabled) {
            val taskIds = selectedOrderIds.toList()
            setOnClickListener {
                DriverSdkProvider.driverSdk().task.createGroup(taskIds).observe(viewLifecycleOwner) {
                    Log.i(TAG, "got create group result for order ids=$taskIds, result=$it")
                    val text = when (it) {
                        is CreateGroupTaskResult.Success -> "Orders merged successfully, new order: ${it.task}"
                        is CreateGroupTaskResult.Error -> "Failed to merge orders, error=${it.error}"
                    }
                    Snackbar.make(this, text, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun refreshUnGroupButton(selectedOrderIds: Selection<Long>) = with(binding.taskListFragmentBottomSheet.btnUngroupOrders) {
        val groupedTasks = DriverSdkProvider.driverSdk().data.taskList.value?.filter { selectedOrderIds.contains(it.getId()) && it.groupUUID.isNotEmpty() } ?: emptyList()
        text = resources.getQuantityString(plurals.un_merge_orders_button, groupedTasks.size, groupedTasks.size)
        isEnabled = groupedTasks.isNotEmpty()
        if (isEnabled) {
            val taskIds = groupedTasks.map { it.getId() }.toList()
            setOnClickListener {
                taskIds.forEach { taskId ->
                    DriverSdkProvider.driverSdk().task.unGroup(taskId).observe(viewLifecycleOwner) {
                        Log.i(TAG, "got ungroup result for order ids=$taskIds, result=$it")
                        val text = when (it) {
                            is UnGroupTaskResult.Success -> "Order unmerged successfully, new orders: ${it.tasks}"
                            is UnGroupTaskResult.Error -> "Failed to unmerge orders, error=${it.error}"
                        }
                        Snackbar.make(this, text, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun startShift(context: Context) {
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), START_SHIFT_WITH_LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        DriverSdkProvider.driverSdk().shift.startShift(object : ResultCallback<ShiftStartResult> {
            override fun onResult(result: ShiftStartResult) {
                if (result.success) {
                    Log.i(TAG, "user is online, DriverSdk is working in the background, DriverSdkProvider.driverSdk.data.online will post TRUE")
                } else {
                    Log.i(TAG, "start shift request failed, error=${result.error}")
                }
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (START_SHIFT_WITH_LOCATION_PERMISSION_REQUEST_CODE == requestCode && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            startShift(requireContext())
        }
    }
}
