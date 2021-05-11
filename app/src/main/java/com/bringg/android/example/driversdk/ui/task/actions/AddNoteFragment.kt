package com.bringg.android.example.driversdk.ui.task.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bringg.android.example.driversdk.R
import driver_sdk.DriverSdkProvider
import driver_sdk.content.ResultCallback
import driver_sdk.driver.actions.DriverActionData
import driver_sdk.driver.model.result.NoteResult
import driver_sdk.logging.BringgLog
import kotlinx.android.synthetic.main.fragment_add_note.*

class AddNoteFragment : Fragment() {

    private val args: AddNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_ok.setOnClickListener { onPositiveButtonClicked() }
        button_cancel.setOnClickListener { onNegativeButtonClicked() }
    }

    private fun onPositiveButtonClicked() {
        val note: String = text_note.text.toString()
        val actionData = DriverActionData.Builder(args.actionData).build()
        BringgLog.info("taskActionItem", "${args.actionData}")
        BringgLog.info("actionData", "$actionData")
        submitNote(actionData, note)
    }

    private fun onNegativeButtonClicked() {
        findNavController().navigateUp()
    }

    private fun submitNote(actionData: DriverActionData, note: String) {
        DriverSdkProvider.driverSdk().actions.submitNote(actionData, note, object :
            ResultCallback<NoteResult> {
            override fun onResult(result: NoteResult) {
                BringgLog.info("Submit Note", "submit note result=$result")
                if (result.success) {
                    findNavController().navigateUp()
                }
            }
        })
    }
}