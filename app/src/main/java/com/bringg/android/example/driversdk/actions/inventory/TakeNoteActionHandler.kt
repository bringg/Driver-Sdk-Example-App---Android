package com.bringg.android.example.driversdk.actions.inventory

import driver_sdk.actions.ActionData

class TakeNoteActionHandler(private val actionsExecutor: ActionsExecutor) : InventoryActionHandler {

    override fun handle(actionData: ActionData) {
        actionsExecutor.takeNote(actionData)
    }
}