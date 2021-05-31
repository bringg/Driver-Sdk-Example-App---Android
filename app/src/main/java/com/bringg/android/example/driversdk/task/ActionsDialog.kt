package com.bringg.android.example.driversdk.task

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import driver_sdk.DriverSdkProvider
import driver_sdk.actions.FormData
import driver_sdk.actions.FormSubmitterCallback
import driver_sdk.content.ResultCallback
import driver_sdk.driver.actions.DriverActionData
import driver_sdk.driver.model.result.NoteResult
import driver_sdk.logging.BringgLog
import driver_sdk.models.configuration.TaskAction
import driver_sdk.models.configuration.forms.Form
import driver_sdk.models.configuration.forms.FormField
import driver_sdk.models.enums.ImageType
import kotlinx.android.synthetic.main.fragment_task_actions_dialog.*
import kotlinx.android.synthetic.main.list_item_task_action.view.*
import org.json.JSONObject

class ActionsDialog : DialogFragment() {

    private val args: ActionsDialogArgs by navArgs()
    private val TAG = "ActionsDialog"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_task_actions_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_actions.adapter = TaskActionsAdapter(
            listOf( TaskAction.TAKE_NOTE, TaskAction.TAKE_PICTURE, TaskAction.FORM )
        ) {
            clickTaskAction(it)
        }
    }

    private fun clickTaskAction(it: View) {
        val actionData = DriverActionData.Builder().taskId(args.taskId).waypointId(args.waypointId).build()
        when (it.tag as TaskAction) {
            TaskAction.TAKE_NOTE -> submitNote(actionData)
            TaskAction.TAKE_PICTURE -> submitImage(actionData)
            TaskAction.FORM -> submitForm(actionData)
        }
    }

    private fun submitNote(actionData: DriverActionData) {
        DriverSdkProvider.driverSdk().actions.submitNote(actionData, "note text", object :
            ResultCallback<NoteResult>{
            override fun onResult(result: NoteResult) {
                if (result.success) {
                    Toast.makeText(context, "Note submitted successfully", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, result.error!!.name, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun submitImage(actionData: DriverActionData) {
        DriverSdkProvider.driverSdk().actions.submitImage(actionData, ImageType.PHOTO, Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565), null,
            object : ResultCallback<NoteResult> {
                override fun onResult(result: NoteResult) {
                    if (result.success) {
                        Toast.makeText(context, "Image submitted successfully", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, result.error!!.name, Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun submitForm(actionData: DriverActionData) {
        val jsonObject = toJson()
        val form = Form(jsonObject, "form title", "submit")
        val formData = FormData.Builder(form, object : FormSubmitterCallback{
            override fun onError(error: FormSubmitterCallback.Error) {
                BringgLog.info(TAG, "submitForm onError")
                Toast.makeText(context, error.name, Toast.LENGTH_LONG).show()
            }

            override fun onFormSubmitted() {
                BringgLog.info(TAG, "submitForm onFormSubmitted")
                Toast.makeText(context, "Form submitted", Toast.LENGTH_LONG).show()
            }

            override fun onMissingValueError(formField: FormField) {
                BringgLog.info(TAG, "submitForm onMissingValueError $formField")
                Toast.makeText(context, "Form missing field $formField", Toast.LENGTH_LONG).show()
            }

            override fun onProgress(progress: Int, total: Int) {
                BringgLog.info(TAG, "submitForm onProgress $progress total: $total")
            }
        }).build()
        DriverSdkProvider.driverSdk().actions.submitForm(actionData, formData)
    }

    private fun toJson(): JSONObject {
        return JSONObject().apply {
            accumulate("title", "title")
            accumulate("form field1", "form field1")
            accumulate("form field2", "form field2")
        }
    }

    class TaskActionsAdapter(private val actions: List<TaskAction>, private val itemClickListener: View.OnClickListener) : RecyclerView.Adapter<TaskActionViewHolder>(
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

        fun bind(taskActionItem: TaskAction) {
            itemView.tag = taskActionItem
            itemView.txt_action_name.text = taskActionItem.name
        }
    }
}