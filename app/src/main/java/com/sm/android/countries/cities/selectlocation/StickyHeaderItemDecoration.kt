package com.sm.android.countries.cities.selectlocation

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class StickyHeaderItemDecoration(
    private val isHeader: (position: Int) -> Boolean,
    private val getHeaderText: (position: Int) -> String
) : RecyclerView.ItemDecoration() {

    private val headerHeight = 90
    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#E8E7E7")
    }


    private val textPaint = Paint().apply {
        color = Color.parseColor("#FF018786")
        textSize = 40f
        isAntiAlias = true
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.childCount == 0) return

        var currentHeaderText: String? = null
        var nextHeaderOffset = headerHeight

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            if (position == RecyclerView.NO_POSITION) continue

            if (isHeader(position)) {
                val headerTop = child.top

                if (headerTop <= headerHeight && headerTop > 0) {
                    nextHeaderOffset = headerTop
                    break
                }
            }
        }

        // Find top visible item to determine current section
        val firstVisiblePosition = (parent.layoutManager as? LinearLayoutManager)
            ?.findFirstVisibleItemPosition() ?: return

        var headerPosition = firstVisiblePosition
        while (headerPosition >= 0) {
            if (isHeader(headerPosition)) {
                currentHeaderText = getHeaderText(headerPosition)
                break
            }
            headerPosition--
        }

        currentHeaderText?.let { header ->
            val topOffset = if (nextHeaderOffset < headerHeight) {
                nextHeaderOffset - headerHeight
            } else {
                0
            }

            // Draw sticky header
            c.drawRect(
                0f,
                topOffset.toFloat(),
                parent.width.toFloat(),
                (topOffset + headerHeight).toFloat(),
                backgroundPaint
            )

            c.drawText(
                header,
                30f,
                (topOffset + headerHeight - 30).toFloat(),
                textPaint
            )
        }
    }
}

