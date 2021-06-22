package com.bringg.android.example.driversdk

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import driver_sdk.account.LoginMerchant
import driver_sdk.actions.FormData
import driver_sdk.content.ResultCallback
import driver_sdk.driver.actions.DriverActionData
import driver_sdk.driver.infrastructure.DriverSdk
import driver_sdk.driver.model.result.CreateGroupTaskResult
import driver_sdk.driver.model.result.DriverLoginResult
import driver_sdk.driver.model.result.NoteResult
import driver_sdk.driver.model.result.PhoneVerificationRequestResult
import driver_sdk.driver.model.result.ResetPasswordRequestResult
import driver_sdk.driver.model.result.ShiftEndResult
import driver_sdk.driver.model.result.ShiftStartResult
import driver_sdk.driver.model.result.TaskAcceptResult
import driver_sdk.driver.model.result.TaskRejectResult
import driver_sdk.driver.model.result.TaskStartResult
import driver_sdk.driver.model.result.UnGroupTaskResult
import driver_sdk.driver.model.result.WaypointArriveResult
import driver_sdk.driver.model.result.WaypointLeaveResult
import driver_sdk.models.WayPointUpdatedDataFromApp
import driver_sdk.models.enums.ImageType
import driver_sdk.tasks.TaskCancelResult

class BringgSdkViewModel(private val driverSdk: DriverSdk) : ViewModel() {
    //region factory
    class Factory(private val driverSdk: DriverSdk) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = BringgSdkViewModel(driverSdk) as T
    }
    //endregion

    private val TAG = "BringgSdkViewModel"

    val data = driverSdk.data

    //region authentication
    fun loginWithEmail(email: String, password: String, resultCallback: ResultCallback<DriverLoginResult>) {
        driverSdk.authentication.loginWithEmail(email, password, resultCallback)
    }

    fun loginWithEmail(email: String, password: String, loginMerchant: LoginMerchant, resultCallback: ResultCallback<DriverLoginResult>) {
        driverSdk.authentication.loginWithEmail(email, password, loginMerchant, resultCallback)
    }

    fun loginWithPhone(phone: String, verificationCode: String, resultCallback: ResultCallback<DriverLoginResult>) {
        driverSdk.authentication.loginWithPhone(phone, verificationCode, resultCallback)
    }

    fun loginWithPhone(phone: String, verificationCode: String, loginMerchant: LoginMerchant, resultCallback: ResultCallback<DriverLoginResult>) {
        driverSdk.authentication.loginWithPhone(phone, verificationCode, loginMerchant, resultCallback)
    }

    fun loginWithTokenAndSecret(region: String, token: String, secret: String, resultCallback: ResultCallback<DriverLoginResult>) {
        driverSdk.authentication.loginWithTokenAndSecret(region, token, secret, resultCallback)
    }

    fun loginWithOIDCToken(region: String, token: String, resultCallback: ResultCallback<DriverLoginResult>) {
        driverSdk.authentication.loginWithOIDCToken(region, token, resultCallback)
    }

    fun requestPhoneVerification(phone: String) {
        driverSdk.authentication.requestPhoneVerification(phone, object : ResultCallback<PhoneVerificationRequestResult> {
            override fun onResult(result: PhoneVerificationRequestResult) {
                if (result.success) {
                    Log.i(
                        TAG, "phone verification request sent to Bringg." +
                            "SMS verification code will be sent to the provided phone number." +
                            "Use the verification code from the SMS message to call DriverSdkProvider.driverSdk.loginWithPhone(phone, verificationCode, callback)"
                    )
                } else {
                    Log.i(TAG, "phone verification request failed")
                }
            }
        })
    }

    fun requestResetPasswordLink(emailAddress: String) {
        driverSdk.authentication.requestResetPasswordLink(emailAddress, object : ResultCallback<ResetPasswordRequestResult> {
            override fun onResult(result: ResetPasswordRequestResult) {
                if (result.success) {
                    Log.i(
                        TAG, "reset password request sent to Bringg." +
                            "A link to reset the password will be sent to the provided email address" +
                            "Use the password to call DriverSdkProvider.driverSdk.loginWithEmail(email, password, callback)"
                    )
                } else {
                    Log.i(TAG, "reset password request failed")
                }
            }
        })
    }

    fun logout() {
        driverSdk.authentication.logout()
    }
    //endregion

    //region shift actions
    fun isOnShift() = driverSdk.isOnShift()

    fun startShift() {
        driverSdk.shift.startShift(object : ResultCallback<ShiftStartResult> {
            override fun onResult(result: ShiftStartResult) {
                if (result.success) {
                    Log.i(TAG, "user is online, DriverSdk is working in the background, DriverSdkProvider.driverSdk.data.online will post TRUE")
                } else {
                    Log.i(TAG, "start shift request failed, error=${result.error}")
                }
            }
        })
        driverSdk.shift.startShift(object : ResultCallback<ShiftStartResult> {
            override fun onResult(result: ShiftStartResult) {
                if (result.success) {
                    Log.i(TAG, "user is online, DriverSdk is working in the background, DriverSdkProvider.driverSdk.data.online will post TRUE")
                } else {
                    Log.i(TAG, "start shift request failed, error=${result.error}")
                }
            }
        })
    }

    fun endShift() {
        driverSdk.shift.endShift(object : ResultCallback<ShiftEndResult> {
            override fun onResult(result: ShiftEndResult) {
                if (result.success) {
                    Log.i(TAG, "user is offline, DriverSdk stopped all background processes, DriverSdkProvider.driverSdk.data.online will post FALSE")
                } else {
                    Log.i(TAG, "end shift request failed, error=${result.error}")
                }
            }
        })
    }
    //endregion

    //region task actions
    fun acceptTask(taskId: Long) {
        driverSdk.task.acceptTask(taskId, object : ResultCallback<TaskAcceptResult> {
            override fun onResult(result: TaskAcceptResult) {
                if (result.success) {
                    Log.i(TAG, "task was successfully accepted, LiveData event will be posted, result=$result")
                } else {
                    Log.i(TAG, "accepting the task failed, error=${result.error}")
                }
            }
        })
    }

    fun rejectTask(taskId: Long) {
        driverSdk.task.rejectTask(taskId, object : ResultCallback<TaskRejectResult> {
            override fun onResult(result: TaskRejectResult) {
                if (result.success) {
                    Log.i(TAG, "task was successfully rejected, LiveData event will be posted, result=$result")
                } else {
                    Log.i(TAG, "rejecting the task failed, error=${result.error}")
                }
            }
        })
    }

    fun startTask(taskId: Long): LiveData<TaskStartResult> {
        return driverSdk.task.startTask(taskId)
    }

    fun arriveToWayPoint(waypointId: Long) {
        driverSdk.task.arriveToWayPoint(waypointId, object : ResultCallback<WaypointArriveResult> {
            override fun onResult(result: WaypointArriveResult) {
                Log.i(TAG, "arrive waypoint result=$result")
            }
        })
    }

    fun leaveWayPoint(waypointId: Long, resultCallback: ResultCallback<WaypointLeaveResult>) {
        driverSdk.task.leaveWayPoint(waypointId, resultCallback)
    }

    fun cancelTask(taskId: Long, selectedReason: String, toString: String, resultCallback: ResultCallback<TaskCancelResult>) {
        driverSdk.task.cancelTask(taskId, selectedReason, toString, resultCallback)
    }

    fun createGroup(taskIds: List<Long>): LiveData<CreateGroupTaskResult> {
        return driverSdk.task.createGroup(taskIds)
    }

    fun unGroup(taskId: Long): LiveData<UnGroupTaskResult> {
        return driverSdk.task.unGroup(taskId)
    }

    fun updateWaypoint(update: WayPointUpdatedDataFromApp) {
        driverSdk.task.updateWaypoint(update)
    }
    //endregion

    // region note actions
    fun submitNote(
        taskId: Long,
        waypointId: Long = 0,
        taskInventoryId: Long = 0,
        text: String,
        callback: ResultCallback<NoteResult>
    ) {
        val actionData = DriverActionData.Builder()
            .taskId(taskId)
            .waypointId(waypointId)
            .inventoryItemId(taskInventoryId)
        driverSdk.actions.submitNote(
            actionData.build(),
            text,
            callback
        )
    }

    fun submitImage(
        taskId: Long,
        waypointId: Long = 0,
        taskInventoryId: Long = 0,
        imageType: ImageType,
        bitmap: Bitmap,
        imageDeletionUri: String?,
        callback: ResultCallback<NoteResult>
    ) {
        val actionData = DriverActionData.Builder()
            .taskId(taskId)
            .waypointId(waypointId)
            .inventoryItemId(taskInventoryId)
        driverSdk.actions.submitImage(
            actionData.build(),
            imageType,
            bitmap,
            imageDeletionUri,
            callback
        )
    }

    fun submitForm(
        taskId: Long,
        waypointId: Long = 0,
        taskInventoryId: Long = 0,
        formData: FormData
    ) {
        val actionData = DriverActionData.Builder()
            .taskId(taskId)
            .waypointId(waypointId)
            .inventoryItemId(taskInventoryId)
        driverSdk.actions.submitForm(
            actionData.build(),
            formData
        )
    }
    //endregion

    //region admin messages
    val adminMessages = driverSdk.adminMessages.messages
    val unreadAdminMessages = driverSdk.adminMessages.unreadMessages

    fun markMessageRead(messageId: Long) {
        driverSdk.adminMessages.markRead(messageId)
    }

    fun deleteMessage(messageId: Long) {
        driverSdk.adminMessages.delete(messageId)
    }
    //endregion
}
