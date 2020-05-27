package com.bringg.android.example.driversdk.actions.inventory

import androidx.collection.SparseArrayCompat
import driver_sdk.controllers.InventoryActionsProvider
import driver_sdk.models.configuration.TaskAction
import driver_sdk.util.annotations.Mockable

@Mockable
class InventoryActionHandlerProvider(
    actionsExecutor: ActionsExecutor,
    inventoryActionsHandler: InventoryActionHandler
) {

    private var actionHandlers: SparseArrayCompat<InventoryActionHandler> = SparseArrayCompat(TaskAction.values().size)

    init {
        actionHandlers.put(TaskAction.FORM.ordinal, FormActionHandler(actionsExecutor))
        actionHandlers.put(TaskAction.TAKE_PICTURE.ordinal, TakePictureActionHandler(actionsExecutor))
        actionHandlers.put(TaskAction.TAKE_NOTE.ordinal, TakeNoteActionHandler(actionsExecutor))
        actionHandlers.put(TaskAction.TAKE_SIGNATURE.ordinal, TakeSignatureActionHandler(actionsExecutor))
        actionHandlers.put(TaskAction.REJECT_INVENTORY.ordinal, RejectInventoryActionHandler(actionsExecutor))
        actionHandlers.put(TaskAction.TAKE_SCAN.ordinal, TakeScanActionHandler(actionsExecutor))
    }

    fun get(taskAction: TaskAction): InventoryActionHandler {
        return if (isSupported(taskAction))
            actionHandlers[taskAction.ordinal]!!
        else UnsupportedActionHandler
    }

    private fun isSupported(taskAction: TaskAction): Boolean {
        return InventoryActionsProvider.isSupported(taskAction)
    }
}