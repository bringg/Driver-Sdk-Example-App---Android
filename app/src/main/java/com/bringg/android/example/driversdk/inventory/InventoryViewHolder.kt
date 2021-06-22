package com.bringg.android.example.driversdk.inventory

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import driver_sdk.models.Inventory

abstract class InventoryViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected open fun getQuantityText(item: Inventory, fullyAppliedParentNotFullyAcceptedOrRejected: Boolean): String {
        if (fullyAppliedParentNotFullyAcceptedOrRejected) return if (item.isPending) isPendingParentPartiallyAppliedText else isNotPendingParentPartiallyAppliedText

        // x of total collected
        var text = if (item.isPending) getIsPendingText(item) else getIsNotPendingText(item)
        val rejectedQuantity = item.rejectedQuantity
        // y rejected
        text = text + " " + getItemsRejectedText(rejectedQuantity)
        return text
    }

    private val isNotPendingParentPartiallyAppliedText: String
        get() = itemView.context.getString(R.string.inventory_parent_partially_delivered)

    private val isPendingParentPartiallyAppliedText: String
        get() = itemView.context.getString(R.string.inventory_parent_partially_collected)

    private fun getIsPendingText(item: Inventory): String = "${item.currentQuantity} of ${item.originalQuantity} collected"

    private fun getIsNotPendingText(item: Inventory): String = "${item.currentQuantity} of ${item.originalQuantity} delivered"

    private fun getItemsRejectedText(rejectedCount: Int): String = " (${rejectedCount} rejected)"

    open fun onDetached() {}
    abstract fun bind(item: Inventory)

}