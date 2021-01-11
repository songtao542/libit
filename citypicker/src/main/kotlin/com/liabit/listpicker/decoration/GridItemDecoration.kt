package com.liabit.listpicker.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(private val spanCount: Int, private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount
        outRect.left = column * space / spanCount
        outRect.right = space - (column + 1) * space / spanCount
        if (position >= spanCount) {
            outRect.top = space
        }
    }
}
