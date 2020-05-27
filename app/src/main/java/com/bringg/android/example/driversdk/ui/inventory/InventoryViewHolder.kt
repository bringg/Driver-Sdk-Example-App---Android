package com.bringg.android.example.driversdk.ui.inventory

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import driver_sdk.models.Inventory

abstract class InventoryViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected open fun getQuantityText(item: Inventory, fullyAppliedParentNotFullyAcceptedOrRejected: Boolean): String {
        if (fullyAppliedParentNotFullyAcceptedOrRejected) return if (item.isPending) isPendingParentPartiallyAppliedText else isNotPendingParentPartiallyAppliedText

        // x of total collected
        var text = if (item.isPending) getIsPendingText(item) else getIsNotPendingText(item)
        val rejectedQuantity = item.rejectedQuantity
        if (rejectedQuantity > 0) {
            // y rejected
            text = text + " " + getItemsRejectedText(rejectedQuantity)
        }
        return text
    }

    private val isNotPendingParentPartiallyAppliedText: String
        get() = itemView.context.getString(R.string.inventory_parent_partially_delivered)

    private val isPendingParentPartiallyAppliedText: String
        get() = itemView.context.getString(R.string.inventory_parent_partially_collected)

    private fun getIsPendingText(item: Inventory): String = "of ${item.originalQuantity} collected"

    private fun getIsNotPendingText(item: Inventory): String = "of ${item.originalQuantity} delivered"

    private fun getItemsRejectedText(rejectedCount: Int): String = "of ${rejectedCount} rejected"

    fun bindReadyText(textView: TextView, isItemPending: Boolean, isItemReady: Boolean) {
        if (isItemReady) {
            textView.text = "✓ " + getDoneItemText(isItemPending)
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
        }
    }

    private fun getDoneItemText(isItemPending: Boolean): String {
        return if (isItemPending) {
            itemView.context.getString(R.string.inventory_ready_to_deliver)
        } else itemView.context.getString(R.string.delivered)
    }

    open fun onDetached() {}
    abstract fun bind(item: Inventory)

}