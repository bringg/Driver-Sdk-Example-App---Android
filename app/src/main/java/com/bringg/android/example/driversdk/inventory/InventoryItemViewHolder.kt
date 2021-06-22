package com.bringg.android.example.driversdk.inventory

import android.view.View
import com.bumptech.glide.Glide
import driver_sdk.models.Inventory
import kotlinx.android.synthetic.main.list_item_inventory_item.view.*

open class InventoryItemViewHolder internal constructor(itemView: View, private val inventoryItemPresenter: InventoryItemPresenter) : InventoryViewHolder(itemView) {

    override fun bind(item: Inventory) {
        itemView.inventory_title.text = item.title
        itemView.inventory_external_id.text = "External Id: ${item.externalId}"
        itemView.inventory_scan_string.text = "Barcode: ${item.scanString} " + (if (item.wasScanned()) " (Scanned)" else "(Not scanned)")
        itemView.inventory_assign_scan_string.text = "Assigned Scan: ${item.assignedScanString}"
        itemView.inventory_comment.text = "Comment: ${item.note}"
        itemView.inventory_price.text = "Cost: " + "%.2f".format(item.cost) + " (unit price: ${item.price})"
        itemView.inventory_weight.text = "Weight: " + "%.2f".format(item.weight)
        itemView.inventory_dimensions.text = "Dimensions: W=${item.width}, H=${item.height}, L=${item.length}"

        bindSubItems(item)
        bindImage(item)

        val allSubInventoriesFullyApplied = item.allSubInventoriesFullyApplied()
        bindQuantity(item, allSubInventoriesFullyApplied)
        itemView.setOnClickListener { inventoryItemPresenter.showEditingView(item) }
    }

    private fun bindQuantity(item: Inventory, allSubInventoriesFullyApplied: Boolean) =
        with(itemView.inventory_quantity_value) {
            text = getQuantityText(item, allSubInventoriesFullyApplied)
        }

    private fun bindSubItems(item: Inventory) = with(itemView.inventory_nested_items_amount) {
        if (item.hasSubInventory()) {
            visibility = View.VISIBLE
            text = "${item.remainingSubItemsCount} >"
            setOnClickListener { inventoryItemPresenter.showSubItems(item) }
        } else {
            visibility = View.GONE
            setOnClickListener(null)
        }
    }

    private fun bindImage(item: Inventory) = with(itemView.inventory_image) {
        if (item.hasImage()) {
            visibility = View.VISIBLE
            Glide.with(this).load(item.image!!).into(this)
        } else {
            visibility = View.GONE
            setImageDrawable(null)
        }
    }
}