package com.bringg.android.example.driversdk.ui.inventory

import androidx.recyclerview.widget.DiffUtil
import driver_sdk.models.Inventory

class InventoryDiffCallback(
    private val oldInventories: List<Inventory>,
    private val newInventories: List<Inventory>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldInventories.size

    override fun getNewListSize() = newInventories.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldInventories[oldItemPosition]
        val newItem = newInventories[newItemPosition]
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldInventory = oldInventories[oldItemPosition]
        val newInventory = newInventories[newItemPosition]
        return oldInventory == newInventory
    }
}