package com.sm.android.countries.cities.selectlocation

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// 4. StickyHeaderItemDecoration.kt
/*class StickyHeaderItemDecoration(
    private val isHeader: (position: Int) -> Boolean,
    private val getHeaderText: (position: Int) -> String
) : RecyclerView.ItemDecoration() {

    private val headerHeight = 100
    private val headerPaint = Paint().apply {
        color = Color.LTGRAY
        textSize = 40f
        typeface = Typeface.DEFAULT_BOLD
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val child = parent.getChildAt(0) ?: return
        val position = parent.getChildAdapterPosition(child)
        val header = getHeaderText(position)

        c.drawRect(left.toFloat(), 0f, right.toFloat(), headerHeight.toFloat(), headerPaint)
        c.drawText(header, left + 30f, headerHeight - 30f, Paint().apply { color = Color.BLACK; textSize = 40f })
    }
}*/

/*class StickyHeaderItemDecoration(
    private val isHeader: (position: Int) -> Boolean,
    private val getHeaderText: (position: Int) -> String
) : RecyclerView.ItemDecoration() {

    private val headerHeight = 100
    private val backgroundPaint = Paint().apply {
        color = Color.LTGRAY
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 40f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        if (childCount == 0) return

        var headerTitle = ""
        var topOffset = 0f

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            if (position == RecyclerView.NO_POSITION) continue

            if (isHeader(position)) {
                headerTitle = getHeaderText(position)
                val nextChildTop = child.top

                if (nextChildTop in 1 until headerHeight) {
                    topOffset = (nextChildTop - headerHeight).toFloat()
                    break
                }
            }
        }

        // Draw sticky header
        c.drawRect(
            0f,
            topOffset,
            parent.width.toFloat(),
            topOffset + headerHeight,
            backgroundPaint
        )

        c.drawText(
            headerTitle,
            30f,
            topOffset + headerHeight - 30f,
            textPaint
        )
    }
}*/

class StickyHeaderItemDecoration(
    private val isHeader: (position: Int) -> Boolean,
    private val getHeaderText: (position: Int) -> String
) : RecyclerView.ItemDecoration() {

    private val headerHeight = 100
    private val backgroundPaint = Paint().apply {
        color = Color.LTGRAY
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 40f
        typeface = Typeface.DEFAULT_BOLD
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

