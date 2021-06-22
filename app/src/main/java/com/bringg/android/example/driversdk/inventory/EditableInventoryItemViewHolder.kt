package com.bringg.android.example.driversdk.inventory

import android.view.View
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.bringg.android.example.driversdk.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import driver_sdk.DriverSdkProvider
import driver_sdk.content.inventory.InventoryQtyChangeResult
import driver_sdk.content.inventory.InventoryQtyChangeResult.*
import driver_sdk.models.Inventory

class EditableInventoryItemViewHolder(itemView: View, inventoryItemPresenter: InventoryItemPresenter) :
    InventoryItemViewHolder(itemView, inventoryItemPresenter), LifecycleOwner, Observer<InventoryQtyChangeResult> {

    private val btnAcceptAllChecked = itemView.findViewById<View>(R.id.btn_accept_all)
    private val btnRejectAllChecked = itemView.findViewById<View>(R.id.btn_reject_all)
    private val txtPartial = itemView.findViewById<TextInputLayout>(R.id.txt_partial)

    private val registry = LifecycleRegistry(this).also { it.currentState = State.CREATED }

    override fun bind(item: Inventory) {
        itemView.setBackgroundResource(R.color.inventory_list_item_selected_background)
        btnAcceptAllChecked.setOnClickListener {
            DriverSdkProvider.driverSdk().inventory.accept(item.id, item.originalQuantity).observe(this, this)
        }
        btnRejectAllChecked.setOnClickListener {
            DriverSdkProvider.driverSdk().inventory.accept(item.id, 0).observe(this, this)
        }
        txtPartial.setEndIconOnClickListener {
            val qty = txtPartial.editText?.text.toString().toIntOrNull()
            if (qty == null) {
                txtPartial.error = "invalid quantity"
                return@setEndIconOnClickListener
            }
            txtPartial.error = null
            DriverSdkProvider.driverSdk().inventory.accept(item.id, qty).observe(this, this)
        }
        super.bind(item)
        registry.currentState = State.RESUMED
    }

    override fun getLifecycle() = registry

    override fun onDetached() {
        super.onDetached()
        registry.currentState = DESTROYED
    }

    companion object {
        const val TAG = "QuantityChooser"
    }

    override fun onChanged(qtyChangeResult: InventoryQtyChangeResult) {
        when (qtyChangeResult) {
            is Success -> bind(qtyChangeResult.inventory)
            is Replacement -> {
                //TODO implement replacement ui for replacing rejected items
            }
            is Error -> Snackbar.make(itemView, "error changing qty, result=$qtyChangeResult", Snackbar.LENGTH_LONG).show()
        }
    }
}