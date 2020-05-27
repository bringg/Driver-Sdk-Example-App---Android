package com.bringg.android.example.driversdk.tasklist

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.util.AddressTypeUtil
import driver_sdk.models.Task
import driver_sdk.util.TimeUtil

class TaskViewHolder(
    itemView: View,
    clickListener: ClickListener
) : RecyclerView.ViewHolder(itemView) {

    interface ClickListener {
        fun onTaskItemClick(task: Task)
    }

    private val TAG = "TaskViewHolder"

    private val typeLabelSecond: TextView
    private val servicePlanLabel: TextView
    private val statusLabel: TextView
    private val timeWindow: TextView
    private val status: View
    private val externalId: TextView
    private val customerName: TextView
    private val title: TextView
    private val typeLabel: TextView
    private val address: TextView
    private val due: TextView
    private val addressSecondLine: TextView
    private val customerAddressName: TextView
    private val customerAddressType: TextView
    private val lateIndication: View
    private val customerAddressSecondLayout: View

    init {
        val view = itemView
        itemView.setOnClickListener { clickListener.onTaskItemClick(it.tag as Task) }
        due = view.findViewById(R.id.task_time)
        lateIndication = view.findViewById(R.id.task_late_indication)
        address = view.findViewById(R.id.task_address)
        addressSecondLine = view.findViewById(R.id.task_address_second_line)
        title = view.findViewById(R.id.task_description)
        status = view.findViewById(R.id.task_status_indication)
        statusLabel = view.findViewById(R.id.task_special_status_label)
        typeLabel = view.findViewById(R.id.task_special_status_second_label)
        typeLabelSecond = view.findViewById(R.id.task_special_status_third_label)
        servicePlanLabel = view.findViewById(R.id.task_service_plan_label)
        timeWindow = view.findViewById(R.id.time_window)
        externalId = view.findViewById(R.id.task_external_id)
        customerName = view.findViewById(R.id.task_customer_name)
        customerAddressName = view.findViewById(R.id.customer_address_name)
        customerAddressType = view.findViewById(R.id.customer_address_type)
        customerAddressSecondLayout = view.findViewById(R.id.task_address_second_layout)
    }

    fun bind(task: Task) {
        itemView.tag = task

        val isStarted = task.isStarted
        itemView.isSelected = isStarted

        // show task late indication
        refreshLateIndication(task)
        // set the title, address and scheduled_at
        title.text = task.title
        setAddress(task)
        setCustomerAddress(task)

        // show task status indication
        if (task.isStarted) {
            status.visibility = View.VISIBLE
        } else {
            status.visibility = View.INVISIBLE
        }
        updateLabels(task)
        updateTimeWindow(task)
        updateExternalId(externalId, task.externalId)
        updateCustomerName(customerName, task)
        updateAddressSecondLine(addressSecondLine, task)
        setAddressSecondLayoutVisibility()
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
                val noEarlierThan = waypoint.noEarlierThan
                val noLaterThan = waypoint.noLaterThan
                val timeWindowText: String = "$noEarlierThan - $noLaterThan"
                timeWindow.text = timeWindowText
                timeWindowVisibility = View.VISIBLE
            } else {
                timeWindowVisibility = View.GONE
            }
        } else {
            Log.e(TAG, "failed to update time window - waypoint is null, task_id: " + task.getId())
            timeWindowVisibility = View.GONE
        }
        timeWindow.visibility = timeWindowVisibility
    }

    private fun updateLabels(task: Task) {
        updateLabelStatus(task)
        updateLabelTaskType(task)
        updateLabelSecondTaskType(task)
        updateLabelServicePlan(task)
    }

    private fun updateLabelStatus(task: Task) {
        if (task.isAssignedAndNotAccepted) {
            statusLabel.visibility = View.VISIBLE
            statusLabel.setBackgroundResource(R.drawable.task_list_item_unaccepted_bg)
            statusLabel.setText(R.string.unaccepted_task)
        } else if (task.isFree) {
            statusLabel.visibility = View.VISIBLE
            statusLabel.setBackgroundResource(R.drawable.task_list_item_grab_bg)
            statusLabel.setText(R.string.button_grab)
            // can set here the onClick listener for the grab if we want to make him into button instead of icon
        } else statusLabel.visibility = View.GONE
    }

    private fun updateLabelTaskType(task: Task) {
        if (task.getTaskTypeId() == null) {
            typeLabel.visibility = View.GONE
            return
        }
        var labelVisibility = View.GONE
        var labelBackgroundResource = 0
        var labelStringResource = 0
        val taskType = task.taskTypeId
        when (taskType) {
            Task.TASK_TYPE_ID_PICKUP, Task.TASK_TYPE_ID_PICKUP_AND_DROP_OFF -> {
                labelVisibility = View.VISIBLE
                labelStringResource = R.string.inventory_collect_label
                labelBackgroundResource = R.drawable.task_list_item_pickup_bg
            }
            Task.TASK_TYPE_ID_DROP_OFF -> {
                labelVisibility = View.VISIBLE
                labelStringResource = R.string.inventory_deliver_label
                labelBackgroundResource = R.drawable.task_list_item_dropoff_bg
            }
        }
        typeLabel.visibility = labelVisibility
        if (labelStringResource != 0) {
            typeLabel.setText(labelStringResource)
        } else {
            typeLabel.text = null
        }
        if (labelBackgroundResource != 0) {
            typeLabel.setBackgroundResource(labelBackgroundResource)
        } else {
            ViewCompat.setBackground(typeLabel, null)
        }
    }

    private fun updateLabelSecondTaskType(task: Task) {
        val isPickupAndDropOffTask = task.getTaskTypeId() != null && task.getTaskTypeId() == Task.TASK_TYPE_ID_PICKUP_AND_DROP_OFF
        if (isPickupAndDropOffTask) {
            typeLabelSecond.setText(R.string.inventory_collect_label)
            typeLabelSecond.setBackgroundResource(R.drawable.task_list_item_dropoff_bg)
            typeLabelSecond.visibility = View.VISIBLE
        } else {
            typeLabelSecond.visibility = View.GONE
        }
    }

    private fun updateLabelServicePlan(task: Task) {
        val name = task.servicePlan?.name
        if (name.isNullOrBlank()) {
            servicePlanLabel.visibility = View.GONE
        } else {
            servicePlanLabel.text = name
            servicePlanLabel.visibility = View.VISIBLE
        }
    }

    private fun setCustomerAddress(task: Task) {
        val waypoint = task.currentWayPoint
        if (waypoint != null) {
            if (waypoint.locationName.isNullOrBlank()) {
                customerAddressName.visibility = View.GONE
            } else {
                customerAddressName.visibility = View.VISIBLE
                customerAddressName.text = waypoint.locationName
            }

            val addressType = AddressTypeUtil.getTextByType(itemView.context, waypoint.addressType)
            customerAddressType.text = addressType
            if (addressType.isBlank()) {
                customerAddressType.visibility = View.GONE
            } else {
                customerAddressType.visibility = View.VISIBLE
            }
        } else {
            customerAddressName.visibility = View.GONE
            customerAddressType.visibility = View.GONE
        }
    }

    private fun setAddress(task: Task) {
        if (task.currentWayPoint != null && task.currentWayPoint!!.isFindMe) {
            address.text = itemView.resources.getString(R.string.find_me_message)
        } else {
            address.text = task.extendedAddress
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
        if (customerAddressType.visibility == View.GONE && addressSecondLine.visibility == View.GONE) {
            customerAddressSecondLayout.visibility = View.GONE
        } else {
            customerAddressSecondLayout.visibility = View.VISIBLE
        }
    }

    private fun refreshLateIndication(task: Task) {
        due.text = TimeUtil.getTaskTimeDynamicFormat(
            itemView.context,
            task.scheduledAt,
            R.string.tomorrow_label_for_date
        )

        if (task.isLate) {
            due.setTextColor(getColor(itemView.context, R.color.list_item_red))
            lateIndication.visibility = View.VISIBLE
        } else {
            due.setTextColor(getColor(itemView.context, R.color.list_item_black))
            lateIndication.visibility = View.GONE
        }
    }

    @ColorInt
    open fun getColor(context: Context, @ColorRes colorResId: Int): Int {
        return ResourcesCompat.getColor(context.resources, colorResId, context.theme)
    }
}
