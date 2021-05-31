package com.bringg.android.example.driversdk.task.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.bringg.android.example.driversdk.R
import driver_sdk.models.Inventory
import driver_sdk.util.annotations.Mockable
import kotlinx.android.synthetic.main.layout_inventory_pricing.view.*

@Mockable
class InventoryPricingLayout : ConstraintLayout {

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        inflate(context, R.layout.layout_inventory_pricing, this)
    }


    fun setData(inventoryList: List<Inventory>, inventoryListPresenter: InventoryListPresenter) {
        list_inventory_items.adapter = InventoryPricingAdapter(inventoryList.sortedBy { it.id }, inventoryListPresenter)
    }
}