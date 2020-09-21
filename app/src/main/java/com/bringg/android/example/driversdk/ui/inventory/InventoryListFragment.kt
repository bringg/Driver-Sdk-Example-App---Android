package com.bringg.android.example.driversdk.ui.inventory

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
import com.bringg.android.example.driversdk.ui.AuthenticatedFragment
import driver_sdk.DriverSdkProvider.driverSdk
import driver_sdk.models.Inventory
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
        inventoryRecyclerView = view.findViewById(R.id.inventory_list)
        inventoryRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        inventoryRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        inventoryAdapter = TaskInventoryRecyclerAdapter(view.context, this)
        inventoryRecyclerView.adapter = inventoryAdapter
        showRootList()
    }

    private fun showRootList() {
        navigationFlow.clear()
        val inventories = ArrayList(driverSdk().data.waypoint(args.waypointId).value!!.inventories)
        inventoryAdapter.setInventoryItems(inventories)
    }

    override fun showEditingView(item: Inventory) {
        val adapterPosition = getInventoryItemAdapterPosition(item.id)
        if (adapterPosition > RecyclerView.NO_POSITION && adapterPosition != inventoryAdapter.getSelectedPosition()) {
            inventoryAdapter.setSelectedPosition(adapterPosition)
            inventoryRecyclerView.smoothScrollToPosition(adapterPosition)
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