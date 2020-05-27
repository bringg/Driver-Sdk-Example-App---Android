package com.bringg.android.example.driversdk.ui.inventory

import driver_sdk.models.Inventory

interface InventoryItemPresenter {
    fun showEditingView(item: Inventory)
    fun showSubItems(item: Inventory)
}
