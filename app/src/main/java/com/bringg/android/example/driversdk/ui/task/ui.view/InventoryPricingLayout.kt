package com.bringg.android.example.driversdk.ui.task.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.bringg.android.example.driversdk.R
import driver_sdk.models.Inventory
import driver_sdk.models.enums.PaymentMethod
import driver_sdk.util.annotations.Mockable
import kotlinx.android.synthetic.main.layout_inventory_pricing.view.*

@Mockable
class InventoryPricingLayout : ConstraintLayout {

    private val pricingFormat = "%.2f"

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

    fun setDeliveryFee(deliveryFee: Double) {
        tv_delivery_fee_value.text = pricingFormat.format(deliveryFee)
    }

    fun setTotal(total: Double) {
        tv_total_value.text = pricingFormat.format(total)
    }

    fun setToBePaid(toBePaid: Double) {
        tv_total_to_be_paid_value.text = pricingFormat.format(toBePaid)
    }

    fun setData(inventoryList: List<Inventory>, inventoryListPresenter: InventoryListPresenter) {
        list_inventory_items.adapter = InventoryPricingAdapter(inventoryList, inventoryListPresenter)
    }

    fun setAmountPaid(paidAmount: Double, paymentMethod: PaymentMethod) {
        tv_amount_paid_label.text = "Amount paid (${paymentMethod.name})"
        tv_amount_paid_value.text = pricingFormat.format(paidAmount)
    }
}