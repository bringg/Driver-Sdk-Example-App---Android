package com.bringg.android.example.driversdk.inventory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import com.bringg.android.example.driversdk.authentication.AuthenticatedFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import driver_sdk.content.inventory.InventoryIncrementQtyResult
import driver_sdk.content.inventory.InventoryIncrementQtyResult.ItemFullyApplied
import driver_sdk.driver.model.result.DriverScanResult
import driver_sdk.driver.model.result.DriverScanResult.InvalidItemScan
import driver_sdk.driver.model.result.DriverScanResult.ItemAlreadyScanned
import driver_sdk.driver.model.result.FindInventoryScanResult.DifferentDestination
import driver_sdk.driver.model.result.FindInventoryScanResult.Error
import driver_sdk.driver.model.result.FindInventoryScanResult.Success
import driver_sdk.models.Inventory
import driver_sdk.models.scan.ScanData
import java.util.ArrayList
import java.util.LinkedList

class InventoryListFragment : AuthenticatedFragment(), InventoryItemPresenter {

    private lateinit var inventoryRecyclerView: RecyclerView
    private lateinit var inventoryAdapter: TaskInventoryRecyclerAdapter
    private val args: InventoryListFragmentArgs by navArgs()

    private val navigationFlow = LinkedList<Inventory>()
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (closeEditMode()) {
                return
            }
            if (navigationFlow.isEmpty()) {
                remove()
                findNavController().navigateUp()
            } else {

                navigationFlow.removeLast()
                if (navigationFlow.isEmpty()) {
                    showRootList()
                } else {
                    showSubItems(navigationFlow.last)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onDetach() {
        onBackPressedCallback.remove()
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_inventory_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        val scanInput = view.findViewById<TextInputLayout>(R.id.txt_scan)
        scanInput.setEndIconOnClickListener {
            val scanData = ScanData(
                scanString = scanInput.editText?.text?.toString().orEmpty(),
                isManual = true
            )
            viewModel.findItemForScanString(args.waypointId, scanData).observe(viewLifecycleOwner) { findItemResult ->
                when (findItemResult) {
                    is Success -> onScanItemFound(scanData, findItemResult)
                    is Error -> Snackbar.make(view, "item scan error, result=$findItemResult", Snackbar.LENGTH_LONG).show()
                    is DifferentDestination -> Snackbar.make(view, "scanned item belongs to a different location, result=$findItemResult", Snackbar.LENGTH_LONG).show()
                }
            }
        }
        inventoryRecyclerView = view.findViewById(R.id.inventory_list)
        inventoryRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        inventoryRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        inventoryAdapter = TaskInventoryRecyclerAdapter(view.context, this)
        inventoryRecyclerView.adapter = inventoryAdapter
        showRootList()
    }

    private fun onScanItemFound(scanData: ScanData, findItemResult: Success) {
        val item = findItemResult.inventory
        // sample use-case:
        // scan once, and keep incrementing the quantity on every following scan
        if (!item.wasScanned()) {
            viewModel.applyScan(item.id, scanData).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is DriverScanResult.Success -> inventoryAdapter.notifyDataSetChanged()
                    is InvalidItemScan,
                    is ItemAlreadyScanned,
                    is DriverScanResult.Error ->
                        Snackbar.make(inventoryRecyclerView, "apply scan error, result=$result", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.incrementInventoryQuantity(item).observe(viewLifecycleOwner) { result ->
            when (result) {
                is InventoryIncrementQtyResult.Success -> inventoryAdapter.notifyDataSetChanged()
                is InventoryIncrementQtyResult.Error ->
                    Snackbar.make(inventoryRecyclerView, "increment quantity error, result=$result", Snackbar.LENGTH_LONG).show()
                is ItemFullyApplied ->
                    Snackbar.make(inventoryRecyclerView, "item reached max qty, result=$result", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun showRootList() {
        navigationFlow.clear()
        val inventories = ArrayList(viewModel.data.waypoint(args.waypointId).value!!.inventories)
        inventoryAdapter.setInventoryItems(inventories)
    }

    override fun showEditingView(item: Inventory) {
        val adapterPosition = getInventoryItemAdapterPosition(item.id)
        if (adapterPosition > RecyclerView.NO_POSITION && adapterPosition != inventoryAdapter.getSelectedPosition()) {
            inventoryAdapter.setSelectedPosition(adapterPosition)
            inventoryRecyclerView.smoothScrollToPosition(adapterPosition)
        } else {
            inventoryAdapter.setSelectedPosition(RecyclerView.NO_POSITION)
        }
    }

    override fun showSubItems(item: Inventory) {
        addToBackStack(item)
        inventoryAdapter.setInventoryItems(item.subInventory)
    }

    private fun addToBackStack(inventory: Inventory) {
        if (navigationFlow.isEmpty() || navigationFlow.last != inventory) {
            navigationFlow.add(inventory)
        }
    }

    private fun closeEditMode(): Boolean {
        if (inventoryAdapter.getSelectedPosition() != RecyclerView.NO_POSITION) {
            inventoryAdapter.clearSelection()
            return true
        }
        return false
    }

    private fun getInventoryItemAdapterPosition(inventoryId: Long): Int {
        val viewHolder = inventoryRecyclerView.findViewHolderForItemId(inventoryId)
        if (viewHolder != null) {
            return viewHolder.adapterPosition
        }
        return RecyclerView.NO_POSITION
    }
}