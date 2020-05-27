package com.bringg.android.example.driversdk.actions.inventory

import driver_sdk.actions.ActionData
import driver_sdk.util.annotations.Mockable

@Mockable
object UnsupportedActionHandler : InventoryActionHandler {

    override fun handle(actionData: ActionData) {
        throw UnsupportedOperationException("unsupported inventory action [${actionData.actionItem.taskAction}]")
    }
}