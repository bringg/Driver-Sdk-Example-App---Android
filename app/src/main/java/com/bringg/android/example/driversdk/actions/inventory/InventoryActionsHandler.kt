package com.bringg.android.example.driversdk.actions.inventory
import android.util.Log
import driver_sdk.actions.ActionData
import driver_sdk.models.configuration.TaskAction
import driver_sdk.util.annotations.Mockable

@Mockable
class InventoryActionsHandler : InventoryActionHandler {

    private lateinit var inventoryActionHandlerProvider: InventoryActionHandlerProvider

    fun initialize(inventoryActionHandlerProvider: InventoryActionHandlerProvider) {
        this.inventoryActionHandlerProvider = inventoryActionHandlerProvider
    }

    override fun handle(actionData: ActionData) {
        val taskAction = actionData.actionItem.taskAction
        if (TaskAction.UNKNOWN == taskAction) {
            Log.e(TAG, "task action is null")
            return
        }

        inventoryActionHandlerProvider
            .get(taskAction)
            .handle(actionData)
    }

    companion object {
        const val TAG = "InventoryActionsHandler"
    }
}