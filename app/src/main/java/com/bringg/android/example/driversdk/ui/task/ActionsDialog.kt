package com.bringg.android.example.driversdk.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import driver_sdk.models.configuration.TaskActionItem
import kotlinx.android.synthetic.main.fragment_task_actions_dialog.*
import kotlinx.android.synthetic.main.list_item_task_action.view.*

class ActionsDialog : DialogFragment() {

    private val args: ActionsDialogArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_task_actions_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_actions.adapter = TaskActionsAdapter(
            args.actions,
            {
                //TODO() handle actions
            }
        )
    }

    class TaskActionsAdapter(private val actions: Array<TaskActionItem>, private val itemClickListener: View.OnClickListener) : RecyclerView.Adapter<TaskActionViewHolder>(
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            TaskActionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_task_action, parent, false), itemClickListener)

        override fun getItemCount() = actions.size

        override fun onBindViewHolder(holder: TaskActionViewHolder, position: Int) = holder.bind(actions[position])

    }

    class TaskActionViewHolder(itemView: View, itemClickListener: View.OnClickListener) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener(itemClickListener)
        }

        fun bind(taskActionItem: TaskActionItem) {
            itemView.tag = taskActionItem
            itemView.txt_action_name.text = taskActionItem.title
        }
    }
}