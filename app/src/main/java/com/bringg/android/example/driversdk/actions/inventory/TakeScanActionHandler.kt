package com.bringg.android.example.driversdk.actions.inventory

import driver_sdk.actions.ActionData

class TakeScanActionHandler(private val actionsExecutor: ActionsExecutor) : InventoryActionHandler {

    override fun handle(actionData: ActionData) {
        actionsExecutor.takeScan(actionData)
    }
}