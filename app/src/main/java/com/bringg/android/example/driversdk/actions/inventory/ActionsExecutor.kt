package com.bringg.android.example.driversdk.actions.inventory

import driver_sdk.actions.ActionData
import driver_sdk.models.InventoryItem
import driver_sdk.models.configuration.InventoryActionItem

interface ActionsExecutor {
    fun openForm(actionData: ActionData)
    fun takePicture(actionData: ActionData)
    fun takeNote(actionData: ActionData)
    fun takeSignature(actionData: ActionData)
    fun markQuestionAsDone(actionData: ActionData)
    fun rejectInventory(actionData: ActionData)
    fun takeScan(actionData: ActionData)
    fun getAllInventoryActions(inventoryItem: InventoryItem): Set<InventoryActionItem>
}