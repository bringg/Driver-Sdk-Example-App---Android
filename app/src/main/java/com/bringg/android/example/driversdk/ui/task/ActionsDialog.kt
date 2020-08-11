package com.bringg.android.example.driversdk.ui.task

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import com.google.android.material.snackbar.Snackbar
import driver_sdk.DriverSdkProvider
import driver_sdk.content.ResultCallback
import driver_sdk.models.configuration.TaskActionItem
import driver_sdk.tasks.TaskCancelResult
import kotlinx.android.synthetic.main.fragment_cancel_task_reason_selection.*
import kotlinx.android.synthetic.main.list_item_cancel_reason.view.*

class ActionsDialog : DialogFragment() {

    private fun taskId() = requireArguments().getLong("task_id")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cancel_task_reason_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_cancel_reasons.adapter = CancelReasonsAdapter(
            requireArguments().getStringArrayList("reasons") ?: emptyList(),
            View.OnClickListener {
                cancelTask(it.tag as String)
            }
        )
    }

    private fun cancelTask(selectedReason: String) {
        DriverSdkProvider.driverSdk().task.cancelTask(taskId(), selectedReason, cancel_task_custom_reason.editText?.text.toString(), object : ResultCallback<TaskCancelResult> {
            override fun onResult(result: TaskCancelResult) {
                Log.i("CancelTask", "cancel task result=$result")
                if (result.success) {
                    findNavController().navigateUp()
                } else if (result.requiredActions.isNotEmpty()) {
                    handleMandatoryCancelTaskActions(result.requiredActions)
                } else {
                    Snackbar.make(rv_cancel_reasons, "Cancel task returned error, result=$result", Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun handleMandatoryCancelTaskActions(actions: Collection<TaskActionItem>) {
//        showActionsDialog(actions)
//        val actionData = DriverActionData.Builder(actionItem).taskId(taskId()).build()
//        DriverSdkProvider.driverSdk().actions.addNote(actionData, "this is my note",
//            object : ResultCallback<NoteResult> {
//                override fun onResult(result: NoteResult) {
//                    Log.i("CancelTask", "note result=$result")
//                }
//            })
    }

    class CancelReasonsAdapter(private val reasons: List<String>, private val itemClickListener: View.OnClickListener) : RecyclerView.Adapter<MerchantViewHolder>(
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MerchantViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_cancel_reason, parent, false), itemClickListener)

        override fun getItemCount() = reasons.size

        override fun onBindViewHolder(holder: MerchantViewHolder, position: Int) = holder.bind(reasons[position])

    }

    class MerchantViewHolder(itemView: View, itemClickListener: View.OnClickListener) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener(itemClickListener)
        }

        fun bind(reason: String) {
            itemView.tag = reason
            itemView.txt_reason.text = reason
        }
    }
}