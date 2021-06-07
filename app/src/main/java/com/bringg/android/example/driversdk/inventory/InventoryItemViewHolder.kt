package com.bringg.android.example.driversdk.inventory

import android.view.View
import com.bumptech.glide.Glide
import driver_sdk.models.Inventory
import driver_sdk.util.annotations.Mockable
import kotlinx.android.synthetic.main.list_item_inventory_item.view.*

@Mockable
open class InventoryItemViewHolder internal constructor(itemView: View, private val inventoryItemPresenter: InventoryItemPresenter) : InventoryViewHolder(itemView) {

    override fun bind(item: Inventory) {
        itemView.inventory_title.text = item.title
        itemView.inventory_id.text = item.externalId
        itemView.inventory_subtitle.text = "${item.originalQuantity} x ${item.title}"
        itemView.inventory_comment.text = item.note
        itemView.inventory_editable_quantity.text = item.currentQuantity.toString()

        bindSubItems(item)
        bindImage(item)
        bindPrice(item)
        bindDimensions(item)
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

    private fun bindPrice(item: Inventory) = with(itemView) {
        inventory_price_separator.visibility = View.VISIBLE
        inventory_price.visibility = View.VISIBLE
        inventory_price.text = "%.2f".format(item.cost)
    }

    private fun bindDimensions(item: Inventory) = with(itemView) {
        inventory_dimensions.text = "W=${item.width}, H=${item.height}, L=${item.length}"
        inventory_dimensions_group.visibility = View.VISIBLE
    }
}