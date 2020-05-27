package com.bringg.android.example.driversdk.ui.inventory

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.bringg.android.example.driversdk.R
import driver_sdk.models.Inventory
import driver_sdk.util.annotations.Mockable

@Mockable
class TaskInventoryRecyclerAdapter(private val context: Context, private val itemPresenter: InventoryItemPresenter) : RecyclerView.Adapter<InventoryViewHolder>() {

    private var selectedPosition: Int = NO_POSITION

    private var items = ArrayList<Inventory>()

    companion object {
        const val TYPE_INVENTORY = 1
        const val TYPE_INVENTORY_SELECTED = 4
        const val TAG = "TaskInventoryRecyclerAdapter"
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return items[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        if (viewType == TYPE_INVENTORY_SELECTED) return EditableInventoryItemViewHolder(inflateView(parent, R.layout.list_item_inventory_item_edit_mode), itemPresenter)
        else return InventoryItemViewHolder(inflateView(parent, R.layout.list_item_inventory_item), itemPresenter)
    }

    override fun onViewDetachedFromWindow(holder: InventoryViewHolder) {
        if (holder.adapterPosition != selectedPosition) {
            holder.onDetached()
        }
        super.onViewDetachedFromWindow(holder)
    }

    private fun inflateView(parent: ViewGroup, @LayoutRes layoutResId: Int) =
        LayoutInflater.from(context).inflate(layoutResId, parent, false)

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int = if (position == selectedPosition) TYPE_INVENTORY_SELECTED else TYPE_INVENTORY

    override fun getItemCount() = items.size

    fun setInventoryItems(inventories: List<Inventory>) {
        clearSelection()
        val diffCallback = InventoryDiffCallback(this.items, inventories)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.items.clear()
        this.items.addAll(inventories)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setSelectedPosition(adapterPosition: Int) {
        clearSelection()
        selectedPosition = adapterPosition
        notifyItemChanged(adapterPosition)
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    fun clearSelection() {
        clearSelectedPosition()
    }

    private fun clearSelectedPosition() {
        if (selectedPosition > NO_POSITION) {
            val position = selectedPosition
            selectedPosition = NO_POSITION
            notifyItemChanged(position)
        }
    }
}