package com.bringg.android.example.driversdk.inventory

import android.view.View
import com.bringg.android.example.driversdk.R
import driver_sdk.models.Inventory

class EditableInventoryItemViewHolder(itemView: View, inventoryItemPresenter: InventoryItemPresenter) : InventoryItemViewHolder(itemView, inventoryItemPresenter) {

    private var quantityChooser: InventoryQuantityChooser = itemView.findViewById(R.id.quantity_chooser)

    override fun bind(item: Inventory) {
        quantityChooser.setItem(item)
        itemView.setBackgroundResource(R.color.inventory_list_item_selected_background)
        super.bind(item)
    }
}