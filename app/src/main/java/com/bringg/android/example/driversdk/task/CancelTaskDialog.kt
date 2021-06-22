package com.bringg.android.example.driversdk.task

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.BringgSdkViewModel
import com.bringg.android.example.driversdk.R
import com.google.android.material.snackbar.Snackbar
import driver_sdk.content.ResultCallback
import driver_sdk.driver.model.result.NoteResult
import driver_sdk.tasks.TaskCancelResult
import kotlinx.android.synthetic.main.fragment_cancel_task_reason_selection.*
import kotlinx.android.synthetic.main.list_item_cancel_reason.view.*

class CancelTaskDialog : DialogFragment() {

    private val viewModel: BringgSdkViewModel by activityViewModels()
    private val args: CancelTaskDialogArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cancel_task_reason_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_cancel_reasons.adapter = CancelReasonsAdapter(
            viewModel.data.task(args.taskId).value?.cancelReasons?.toList() ?: emptyList()
        ) {
            cancelTask(it.tag as String)
        }
    }

    private fun cancelTask(selectedReason: String) {
        viewModel.cancelTask(args.taskId, selectedReason, cancel_task_custom_reason.editText?.text.toString(), object : ResultCallback<TaskCancelResult> {
            override fun onResult(result: TaskCancelResult) {
                Log.i("CancelTask", "cancel task result=$result")
                when {
                    result.success -> {
                        findNavController().navigateUp()
                    }
                    result.requiredActions.isNotEmpty() -> {
                        handleMandatoryCancelTaskActions()
                    }
                    else -> {
                        Snackbar.make(rv_cancel_reasons, "Cancel task returned error, result=$result", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun handleMandatoryCancelTaskActions() {
        viewModel.submitNote(
            taskId = args.taskId,
            text = "this is my note",
            callback = object : ResultCallback<NoteResult> {
                override fun onResult(result: NoteResult) {
                    Log.i("Cancel task", "note result=$result")
                }
            }
        )
    }

    class CancelReasonsAdapter(private val reasons: List<String>, private val itemClickListener: View.OnClickListener) : RecyclerView.Adapter<CancelReasonViewHolder>(
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            CancelReasonViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_cancel_reason, parent, false), itemClickListener)

        override fun getItemCount() = reasons.size

        override fun onBindViewHolder(holder: CancelReasonViewHolder, position: Int) = holder.bind(reasons[position])

    }

    class CancelReasonViewHolder(itemView: View, itemClickListener: View.OnClickListener) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener(itemClickListener)
        }

        fun bind(reason: String) {
            itemView.tag = reason
            itemView.txt_reason.text = reason
        }
    }
}