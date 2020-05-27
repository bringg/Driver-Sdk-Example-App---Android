package com.bringg.android.example.driversdk.util

import android.content.Context
import com.bringg.android.example.driversdk.R
import driver_sdk.models.enums.AddressType

class AddressTypeUtil {
    companion object {
        @JvmStatic
        fun getTextByType(context: Context, type: AddressType): String {
            return when (type) {
                AddressType.ADDRESS_TYPE_COMMERCIAL -> context.getString(R.string.address_type_commercial)
                AddressType.ADDRESS_TYPE_RESIDENTIAL -> context.getString(R.string.address_type_residential)
                AddressType.ADDRESS_TYPE_EDUCATIONAL -> context.getString(R.string.address_type_educational)
                AddressType.ADDRESS_TYPE_GOVERNMENT -> context.getString(R.string.address_type_government)
                AddressType.ADDRESS_TYPE_MEDICAL -> context.getString(R.string.address_type_medical)
                AddressType.ADDRESS_TYPE_INDUSTRIAL -> context.getString(R.string.address_type_industrial)
                else -> ""
            }
        }
    }
}