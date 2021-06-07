package com.bringg.android.example.driversdk.adminmessages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import com.bringg.android.example.driversdk.BringgSdkViewModel
import com.bringg.android.example.driversdk.databinding.ListItemAdminMessageBinding
import driver_sdk.content.announcements.Message

class AdminMessageAdapter(private val bringgSdkViewModel: BringgSdkViewModel) : ListAdapter<Message, MessageViewHolder>(object : ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Message, newItem: Message) =
        oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(ListItemAdminMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false), bringgSdkViewModel)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }
}
