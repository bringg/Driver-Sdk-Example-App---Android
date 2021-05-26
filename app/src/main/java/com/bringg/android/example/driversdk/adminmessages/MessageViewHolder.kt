package com.bringg.android.example.driversdk.adminmessages

import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.BringgSdkViewModel
import com.bringg.android.example.driversdk.databinding.ListItemAdminMessageBinding
import driver_sdk.content.announcements.Message

class MessageViewHolder(
    private val binding: ListItemAdminMessageBinding,
    private val bringgSdkViewModel: BringgSdkViewModel
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(message: Message) = with(binding) {
        adminMessageAuthor.text = message.author
        adminMessageText.text = message.message
        adminMessageTimestamp.text = message.time
        adminMessageText.setTypeface(adminMessageText.typeface, if (message.wasRead()) Typeface.NORMAL else Typeface.BOLD)
        adminMessageDeleteBtn.setOnClickListener {
            bringgSdkViewModel.deleteMessage(message.id)
        }
    }
}
