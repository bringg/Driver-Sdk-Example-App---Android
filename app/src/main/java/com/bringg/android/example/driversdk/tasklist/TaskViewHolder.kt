package com.bringg.android.example.driversdk.tasklist

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.BringgSdkViewModel
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.databinding.ListItemTaskBinding
import com.bringg.android.example.driversdk.util.AddressTypeUtil
import driver_sdk.models.Task
import driver_sdk.util.TimeUtil
import java.sql.Date
import java.text.SimpleDateFormat

class TaskViewHolder(
    private val binding: ListItemTaskBinding,
    private val viewModel: BringgSdkViewModel,
    private val lifecycleOwner: LifecycleOwner,
    clickListener: ClickListener
) : RecyclerView.ViewHolder(binding.root) {

    private val dateFormat = SimpleDateFormat.getDateTimeInstance()

    interface ClickListener {
        fun onTaskItemClick(task: Task)
    }

    private val TAG = "TaskViewHolder"

    init {
        itemView.setOnClickListener { clickListener.onTaskItemClick(it.tag as Task) }
    }

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
        object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int = adapterPosition
            override fun getSelectionKey(): Long = (itemView.tag as Task?)?.getId() ?: -1
        }

    fun bind(task: Task, selected: Boolean) {
        itemView.tag = task
        itemView.isActivated = selected

        val isStarted = task.isStarted
        itemView.isSelected = isStarted

        // show task late indication
        refreshLateIndication(task)
        // set the title, address and scheduled_at
        binding.taskDescription.text = task.title
        setAddress(task)
        setCustomerAddress(task)

        // show task status indication
        if (task.isStarted) {
            binding.taskSpecialStatusLabel.visibility = View.VISIBLE
        } else {
            binding.taskSpecialStatusLabel.visibility = View.INVISIBLE
        }
        updateLabels(task)
        updateTimeWindow(task)
        updateExternalId(binding.taskExternalId, task.externalId)
        updateCustomerName(binding.taskCustomerName, task)
        updateAddressSecondLine(binding.taskAddressSecondLine, task)
        setAddressSecondLayoutVisibility()
        viewModel.data.extras.taskExtras(task.getId()).observe(lifecycleOwner) {
            binding.taskExtras.text = it?.toString(5) ?: "null"
        }
    }

    private fun updateExternalId(externalIdView: TextView, externalId: String?) {
        if (TextUtils.isEmpty(externalId)) {
            externalIdView.visibility = View.GONE
        } else {
            externalIdView.visibility = View.VISIBLE
            externalIdView.text = externalId
        }
    }

    private fun updateCustomerName(customerNameView: TextView, task: Task) {
        val customerNameVisibility: Int
        val lastWayPoint = task.lastWayPoint
        if (lastWayPoint == null || TextUtils.isEmpty(lastWayPoint.customerName)) {
            customerNameVisibility = View.GONE
        } else {
            customerNameView.text = lastWayPoint.customerName
            customerNameVisibility = View.VISIBLE
        }
        customerNameView.visibility = customerNameVisibility
    }

    private fun updateTimeWindow(task: Task) {
        val timeWindowVisibility: Int
        var waypoint = task.currentWayPoint
        if (waypoint == null) {
            waypoint = task.firstWayPoint
        }
        if (waypoint != null) {
            if (waypoint.hasTimeWindow()) {
                val noEarlierThan = dateFormat.format(Date(waypoint.timeWindowStart))
                val noLaterThan = dateFormat.format(Date(waypoint.timeWindowEnd))
                val timeWindowText = "$noEarlierThan - $noLaterThan"
                binding.timeWindow.text = timeWindowText
                timeWindowVisibility = View.VISIBLE
            } else {
                timeWindowVisibility = View.GONE
            }
        } else {
            Log.e(TAG, "failed to update time window - waypoint is null, task_id: " + task.getId())
            timeWindowVisibility = View.GONE
        }
        binding.timeWindow.visibility = timeWindowVisibility
    }

    private fun updateLabels(task: Task) {
        updateLabelStatus(task)
        updateLabelTaskType(task)
        updateLabelSecondTaskType(task)
        updateLabelServicePlan(task)
    }

    private fun updateLabelStatus(task: Task) {
        with(binding.taskSpecialStatusLabel) {
            when {
                task.isAssignedAndNotAccepted -> {
                    visibility = View.VISIBLE
                    setBackgroundResource(R.drawable.task_list_item_unaccepted_bg)
                    setText(R.string.unaccepted_task)
                }
                task.isFree -> {
                    visibility = View.VISIBLE
                    setBackgroundResource(R.drawable.task_list_item_grab_bg)
                    setText(R.string.button_grab)
                    // can set here the onClick listener for the grab if we want to make him into button instead of icon
                }
                else -> visibility = View.GONE
            }
        }
    }

    private fun updateLabelTaskType(task: Task) {
        if (task.taskTypeId == null) {
            binding.taskSpecialStatusSecondLabel.visibility = View.GONE
            return
        }
        var labelVisibility = View.GONE
        var labelBackgroundResource = 0
        var labelStringResource = 0
        val firstWayPoint = task.firstWayPoint
        when (task.taskTypeId) {
            Task.TASK_TYPE_ID_PICKUP -> {
                labelVisibility = View.VISIBLE
                labelStringResource = R.string.inventory_collect_label
                labelBackgroundResource = R.drawable.task_list_item_pickup_bg
            }
            Task.TASK_TYPE_ID_PICKUP_AND_DROP_OFF -> {
                if (firstWayPoint != null && !firstWayPoint.isDone) {
                    labelVisibility = View.VISIBLE
                    labelStringResource = R.string.inventory_collect_label
                    labelBackgroundResource = R.drawable.task_list_item_pickup_bg
                }
            }
            Task.TASK_TYPE_ID_DROP_OFF -> {
                if (task.wayPoints.size > 1 && firstWayPoint == task.currentWayPoint) {
                    labelVisibility = View.VISIBLE
                    labelStringResource = R.string.inventory_collect_label
                    labelBackgroundResource = R.drawable.task_list_item_pickup_bg
                } else {
                    labelVisibility = View.VISIBLE
                    labelStringResource = R.string.inventory_deliver_label
                    labelBackgroundResource = R.drawable.task_list_item_dropoff_bg
                }
            }
        }
        with(binding.taskSpecialStatusSecondLabel) {
            visibility = labelVisibility
            if (labelStringResource != 0) {
                setText(labelStringResource)
            } else {
                text = null
            }
            if (labelBackgroundResource != 0) {
                setBackgroundResource(labelBackgroundResource)
            } else {
                background = null
            }
        }
    }

    private fun updateLabelSecondTaskType(task: Task) {
        val isPickupAndDropOffTask = task.taskTypeId != null && task.taskTypeId == Task.TASK_TYPE_ID_PICKUP_AND_DROP_OFF
        with(binding.taskSpecialStatusThirdLabel) {
            if (isPickupAndDropOffTask) {
                setText(R.string.inventory_deliver_label)
                setBackgroundResource(R.drawable.task_list_item_dropoff_bg)
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE
            }
        }
    }

    private fun updateLabelServicePlan(task: Task) {
        val name = task.servicePlan?.name
        with(binding.taskServicePlanLabel) {
            if (name.isNullOrBlank()) {
                visibility = View.GONE
                if (task.groupUUID.isNotEmpty()) {
                    visibility = View.VISIBLE
                    text = "Grouped task"
                }
            } else {
                text = name
                visibility = View.VISIBLE
            }
        }
    }

    private fun setCustomerAddress(task: Task) {
        val waypoint = task.currentWayPoint
        if (waypoint != null) {
            if (waypoint.locationName.isBlank()) {
                binding.customerAddressName.visibility = View.GONE
            } else {
                binding.customerAddressName.visibility = View.VISIBLE
                binding.customerAddressName.text = waypoint.locationName
            }

            val addressType = AddressTypeUtil.getTextByType(itemView.context, waypoint.addressType)
            binding.customerAddressType.text = addressType
            if (addressType.isBlank()) {
                binding.customerAddressType.visibility = View.GONE
            } else {
                binding.customerAddressType.visibility = View.VISIBLE
            }
        } else {
            binding.customerAddressName.visibility = View.GONE
            binding.customerAddressType.visibility = View.GONE
        }
    }

    private fun setAddress(task: Task) {
        with(binding.taskAddress) {
            if (task.currentWayPoint != null && task.currentWayPoint!!.isFindMe) {
                text = itemView.resources.getString(R.string.find_me_message)
            } else {
                text = task.extendedAddress
            }
        }
    }

    private fun updateAddressSecondLine(view: TextView, task: Task) {
        val waypoint = task.currentWayPoint
        if (waypoint != null && !TextUtils.isEmpty(waypoint.secondLineAddress)) {
            view.visibility = View.VISIBLE
            view.text = waypoint.secondLineAddress
        } else {
            view.visibility = View.GONE
        }
    }

    private fun setAddressSecondLayoutVisibility() {
        if (binding.customerAddressType.visibility == View.GONE && binding.taskAddressSecondLine.visibility == View.GONE) {
            binding.taskAddressSecondLayout.visibility = View.GONE
        } else {
            binding.taskAddressSecondLayout.visibility = View.VISIBLE
        }
    }

    private fun refreshLateIndication(task: Task) {
        binding.taskTime.text = TimeUtil.getTaskTimeDynamicFormat(
            itemView.context,
            task.scheduledAt,
            R.string.tomorrow_label_for_date
        )

        if (task.isLate) {
            binding.taskTime.setTextColor(getColor(itemView.context, R.color.list_item_red))
            binding.taskLateIndication.visibility = View.VISIBLE
        } else {
            binding.taskTime.setTextColor(getColor(itemView.context, R.color.list_item_black))
            binding.taskLateIndication.visibility = View.GONE
        }
    }

    @ColorInt
    fun getColor(context: Context, @ColorRes colorResId: Int): Int {
        return ResourcesCompat.getColor(context.resources, colorResId, context.theme)
    }
}
