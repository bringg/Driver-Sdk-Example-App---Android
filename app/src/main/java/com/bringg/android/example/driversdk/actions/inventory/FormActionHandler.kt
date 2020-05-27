package com.bringg.android.example.driversdk.actions.inventory

import driver_sdk.actions.ActionData
import driver_sdk.util.annotations.Mockable

@Mockable
class FormActionHandler(private val actionsExecutor: ActionsExecutor) : InventoryActionHandler {

    override fun handle(actionData: ActionData) {
        actionsExecutor.openForm(actionData)
    }
}