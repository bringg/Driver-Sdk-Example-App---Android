package com.bringg.android.example.driversdk.actions.inventory

import driver_sdk.actions.ActionData

interface InventoryActionHandler {
    fun handle(actionData: ActionData)
}