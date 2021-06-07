package com.bringg.android.example.driversdk.util

import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bringg.android.example.driversdk.R
import com.skydoves.expandablelayout.ExpandableLayout

fun ExpandableLayout.toggleExpandableLayout() {
    if (isExpanded) {
        collapse()
    } else {
        secondLayout.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        expand(secondLayout.measuredHeight)
    }
}

fun ExpandableLayout.remeasure(recyclerView: RecyclerView) {
    val itemCount = recyclerView.adapter?.itemCount
    val emptyView = secondLayout.findViewById<View>(R.id.empty)
    emptyView.visibility =
        if (itemCount != null && itemCount > 0) View.GONE else View.VISIBLE

    if (isExpanded) {
        val currentHeight = secondLayout.measuredHeight
        secondLayout.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val newHeight = secondLayout.measuredHeight
        val diff = newHeight - currentHeight
        Log.e("gil", "current=$currentHeight, new=$newHeight, diff=$diff")
        ValueAnimator.ofInt(height, height + diff).apply {
            duration = 300
            interpolator = LinearInterpolator()
            addUpdateListener {
                val value = it.animatedValue as Int
                secondLayout.updateLayoutParams {
                    height = value + parentLayout.height
                }
            }
        }.start()
    }
}

/**
 * Executes [block] with the View's layoutParams and reassigns the layoutParams with the
 * updated version.
 *
 * @see View.getLayoutParams
 * @see View.setLayoutParams
 **/
internal inline fun View.updateLayoutParams(block: ViewGroup.LayoutParams.() -> Unit) {
    updateLayoutParam(this, block)
}

/**
 * Executes [block] with a typed version of the View's layoutParams and reassigns the
 * layoutParams with the updated version.
 *
 * @see View.getLayoutParams
 * @see View.setLayoutParams
 **/
private inline fun <reified T : ViewGroup.LayoutParams> updateLayoutParam(
    view: View,
    block: T.() -> Unit
) {
    val params = view.layoutParams as T
    block(params)
    view.layoutParams = params
}
