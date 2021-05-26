package com.bringg.android.example.driversdk.util

import android.util.Log
import android.view.View
import android.widget.FrameLayout.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import com.skydoves.expandablelayout.ExpandableLayout

fun ExpandableLayout.toggleExpandableLayout(recyclerView: RecyclerView) {
    if (isExpanded) {
        collapse()
    } else {
        val itemCount = recyclerView.adapter?.itemCount
        val emptyView = secondLayout.findViewById<View>(R.id.empty)
        emptyView.visibility =
            if (itemCount != null && itemCount > 0) View.GONE else View.VISIBLE
        secondLayout.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        expand(secondLayout.measuredHeight)
    }
}

fun ExpandableLayout.remeasure(recyclerView: RecyclerView) {
    if (isExpanded) {
        secondLayout.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val newHeight = secondLayout.measuredHeight
        val parentHeight = parentLayout.height
        val currentHeight = height - parentHeight
        Log.e("gil", "current=$currentHeight, new=$newHeight, parentHeight=$parentHeight")
    }
}