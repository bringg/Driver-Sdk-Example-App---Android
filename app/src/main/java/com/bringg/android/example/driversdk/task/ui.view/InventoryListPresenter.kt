package com.bringg.android.example.driversdk.task.ui.view

import driver_sdk.models.Inventory

interface InventoryListPresenter {
    fun showInventoryList(inventory: Inventory)
}
