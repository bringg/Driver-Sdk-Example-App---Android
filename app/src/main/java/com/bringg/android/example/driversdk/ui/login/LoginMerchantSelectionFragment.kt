package com.bringg.android.example.driversdk.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import driver_sdk.account.LoginMerchant
import kotlinx.android.synthetic.main.fragment_login_merchant_selection.*
import kotlinx.android.synthetic.main.list_item_merchant_selection.view.*

class LoginMerchantSelectionFragment() : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_merchant_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        rv_login_merchant_selection.adapter = MerchantsAdapter(
            requireArguments().getParcelableArray("merchants") as Array<LoginMerchant>,
            View.OnClickListener {
                navController.previousBackStackEntry?.savedStateHandle?.set("merchant", it.tag as LoginMerchant)
                navController.navigateUp()
            }
        )
    }

    class MerchantsAdapter(private val merchants: Array<LoginMerchant>, private val itemClickListener: View.OnClickListener) : RecyclerView.Adapter<MerchantViewHolder>(
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MerchantViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_merchant_selection, parent, false), itemClickListener)

        override fun getItemCount() = merchants.size

        override fun onBindViewHolder(holder: MerchantViewHolder, position: Int) = holder.bind(merchants[position])

    }

    class MerchantViewHolder(itemView: View, itemClickListener: View.OnClickListener) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener(itemClickListener)
        }

        fun bind(loginMerchant: LoginMerchant) {
            itemView.tag = loginMerchant
            itemView.merchant_name.text = loginMerchant.name
        }
    }
}