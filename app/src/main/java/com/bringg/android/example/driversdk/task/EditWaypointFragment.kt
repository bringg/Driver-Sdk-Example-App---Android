package com.bringg.android.example.driversdk.task

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.FOCUS_DOWN
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.authentication.AuthenticatedFragment
import com.bringg.android.example.driversdk.databinding.FragmentEditWaypointBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.Place.Field.ADDRESS
import com.google.android.libraries.places.api.model.Place.Field.ADDRESS_COMPONENTS
import com.google.android.libraries.places.api.model.Place.Field.ID
import com.google.android.libraries.places.api.model.Place.Field.LAT_LNG
import com.google.android.libraries.places.api.model.Place.Field.NAME
import com.google.android.libraries.places.api.model.Place.Field.PHONE_NUMBER
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import driver_sdk.models.WayPointUpdatedDataFromApp
import driver_sdk.models.Waypoint
import driver_sdk.util.ext.ifEmpty
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar

open class EditWaypointFragment : AuthenticatedFragment() {

    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var placesClient: PlacesClient
    private var place: Place? = null
    private val args: EditWaypointFragmentArgs by navArgs()
    private val dateFormat = SimpleDateFormat.getDateTimeInstance()

    private var _binding: FragmentEditWaypointBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Places.initialize(context.applicationContext, context.getString(R.string.maps_api_key))
        placesClient = Places.createClient(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentEditWaypointBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWayPointDetails()
        setTimeWindowListeners()
    }

    private fun initAddressAutoComplete(waypoint: Waypoint) {
        autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        val bounds = RectangularBounds.newInstance(
            LatLng(waypoint.lat - 10.0, waypoint.lng - 10.0),
            LatLng(waypoint.lat + 10.0, waypoint.lng + 10.0),
        )
        autocompleteFragment.setText(place?.address ?: waypoint.extendedAddress)
        autocompleteFragment.setLocationBias(bounds)
        autocompleteFragment.setPlaceFields(
            listOf(
                ADDRESS,
                ADDRESS_COMPONENTS,
                ID,
                LAT_LNG,
                PHONE_NUMBER,
                NAME
            )
        )
        autocompleteFragment.setHint("Pick Address")
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i("placesFragment", "Place: $place")
                updateSelectedPlace(place, waypoint)
            }

            override fun onError(status: Status) {
                Log.i("placesFragment", "An error occurred: $status")
            }
        })
    }

    private fun updateSelectedPlace(place: Place?, waypoint: Waypoint) {
        this.place = place
        initAddressAutoComplete(waypoint)
        val addressComponents = place?.addressComponents?.asList()
        val name = waypoint.customer?.getName() ?: waypoint.customerName
        val phone = waypoint.customer?.phone ?: waypoint.phone
        val email = waypoint.customer?.email ?: waypoint.email
        with(binding) {
            customerEmail.setText(email)
            addressStreet.setText(addressComponents?.firstOrNull { it.types.contains("route") }?.name.ifEmpty { waypoint.street })
            addressCity.setText(addressComponents?.firstOrNull { it.types.contains("locality") }?.name.ifEmpty { waypoint.city })
            houseNumber.setText(addressComponents?.firstOrNull { it.types.contains("street_number") }?.name.ifEmpty { waypoint.houseNumber })
            zipcode.setText(addressComponents?.firstOrNull { it.types.contains("postal_code") }?.name.ifEmpty { waypoint.houseNumber })
        }
        if (waypoint.secondLineAddress.isNullOrBlank()) {
            binding.addressSecondLine.setText(addressComponents?.firstOrNull { it.types.contains("administrative_area_level_1") }?.name?.ifEmpty { waypoint.secondLineAddress })
        } else {
            binding.addressSecondLine.setText(waypoint.secondLineAddress)
        }

        if (name.isBlank()) {
            binding.customerName.setText(place?.name.orEmpty())
        } else {
            binding.customerName.setText(name)
        }
        if (phone.isBlank()) {
            binding.customerPhone.setText(place?.phoneNumber.orEmpty())
        } else {
            binding.customerPhone.setText(name)
        }
    }

    private fun updateWayPointDetails() {
        viewModel.data.waypoint(args.waypointId).observe(viewLifecycleOwner) {
            it?.let { waypoint ->
                updateSelectedPlace(place, waypoint)
                if (waypoint.timeWindowStart > 0)
                    binding.startTimeWindow.setText(dateFormat.format(Date(waypoint.timeWindowStart)))
                if (waypoint.timeWindowEnd > 0)
                    binding.endTimeWindow.setText(dateFormat.format(Date(waypoint.timeWindowEnd)))
                binding.editWaypointBtnSubmit.setOnClickListener {
                    onDoneClicked(waypoint)
                }
            }
        }
    }

    private fun setTimeWindowListeners() {
        binding.startTimeWindow.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showDateTimePicker(getString(R.string.edit_task_hint_start_time_window), v as EditText)
            }
        }
        binding.endTimeWindow.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showDateTimePicker(getString(R.string.edit_task_hint_end_time_window), v as EditText)
            }
        }
    }

    private fun showDateTimePicker(title: String, view: EditText) {
        val isSystem24Hour = is24HourFormat(view.context)
        val dialog = MaterialDatePicker.Builder.datePicker().setTitleText(title).build()
        dialog.addOnCancelListener { view.clearFocus() }
        dialog.addOnPositiveButtonClickListener {
            val calendar = GregorianCalendar.getInstance()
            calendar.timeInMillis = it
            Log.i("datepicker", "selection=$it")
            val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(clockFormat)
                    .setTitleText(title)
                    .build()

            picker.addOnCancelListener { view.clearFocus() }
            picker.addOnPositiveButtonClickListener {
                calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
                calendar.set(Calendar.MINUTE, picker.minute)
                val dateFormat = SimpleDateFormat.getDateTimeInstance()
                view.setText(dateFormat.format(calendar.time))
                view.focusSearch(FOCUS_DOWN).requestFocus()
            }
            picker.show(childFragmentManager, "edit_waypoint_time_picker")
        }
        dialog.show(childFragmentManager, "edit_waypoint_date_picker")
    }

    open fun onDoneClicked(waypoint: Waypoint) {
        val changedData = getChangedData(waypoint)
        if (!changedData.isEmpty()) {
            viewModel.updateWaypoint(changedData)
        }
        findNavController().navigateUp()
    }

    internal open fun getChangedData(waypoint: Waypoint): WayPointUpdatedDataFromApp {
        val builder = WayPointUpdatedDataFromApp.Builder(waypoint.taskId, waypoint.id)
            // time window:
            .startTimeWindow(dateFormat.parse(binding.startTimeWindow.text.toString()).time)
            .endTimeWindow(dateFormat.parse(binding.endTimeWindow.text.toString()).time)
            // customer:
            .customerName(binding.customerName.text.toString())
            .customerEmail(binding.customerEmail.text.toString())
            .customerPhone(binding.customerPhone.text.toString())
            // location
            .lat(place?.latLng?.latitude ?: waypoint.lat)
            .lng(place?.latLng?.longitude ?: waypoint.lng)
            // full formatted address
            .address(place?.address ?: waypoint.extendedAddress)
            // when not using Place object, address should be updated using all following:
            .street(binding.addressStreet.text.toString())
            .city(binding.addressCity.text.toString())
            .houseNumber(binding.houseNumber.text.toString())
            .zipCode(binding.zipcode.text.toString())
            .secondLineAddress(binding.addressSecondLine.text.toString())
        return builder.build()
    }
}