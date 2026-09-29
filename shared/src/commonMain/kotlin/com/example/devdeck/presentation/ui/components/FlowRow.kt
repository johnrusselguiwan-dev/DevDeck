package com.example.devdeck.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * A flexible flow row layout that places children horizontally and wraps them onto subsequent lines
 * when they exceed the available width.
 */
@Composable
fun FlowRowLayout(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val hSpacing = horizontalSpacing.roundToPx()
        val vSpacing = verticalSpacing.roundToPx()

        val rows = mutableListOf<MutableList<Placeable>>()
        val rowHeights = mutableListOf<Int>()

        var currentRow = mutableListOf<Placeable>()
        var currentWidth = 0
        var currentMaxHeight = 0

        measurables.forEach { measurable ->
            val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
            if (currentRow.isNotEmpty() && currentWidth + hSpacing + placeable.width > constraints.maxWidth) {
                rows.add(currentRow)
                rowHeights.add(currentMaxHeight)
                currentRow = mutableListOf()
                currentWidth = 0
                currentMaxHeight = 0
            }
            if (currentRow.isNotEmpty()) {
                currentWidth += hSpacing
            }
            currentRow.add(placeable)
            currentWidth += placeable.width
            currentMaxHeight = max(currentMaxHeight, placeable.height)
        }
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
            rowHeights.add(currentMaxHeight)
        }

        val totalHeight = rowHeights.sum() + max(0, rows.size - 1) * vSpacing
        val width = if (rows.isEmpty()) 0 else constraints.maxWidth

        layout(width, totalHeight) {
            var y = 0
            rows.forEachIndexed { rowIndex, row ->
                var x = 0
                val rowHeight = rowHeights[rowIndex]
                row.forEach { placeable ->
                    placeable.placeRelative(x, y + (rowHeight - placeable.height) / 2)
                    x += placeable.width + hSpacing
                }
                y += rowHeight + vSpacing
            }
        }
    }
}
