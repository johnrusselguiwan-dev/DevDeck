package com.example.devdeck.presentation.ui.home

internal data class SectionBounds(val index: Int, val offset: Int, val size: Int)

internal fun activePortfolioSection(
    items: List<SectionBounds>,
    viewportStart: Int,
    viewportEnd: Int,
    atStart: Boolean,
    atEnd: Boolean
): PortfolioSection {
    if (items.isEmpty() || atStart) return PortfolioSection.ABOUT
    if (atEnd && items.any { it.index == PortfolioSection.CONTACT.index }) return PortfolioSection.CONTACT

    val viewportHeight = (viewportEnd - viewportStart).coerceAtLeast(1)
    val focalY = viewportStart + (viewportHeight * 0.25f).toInt()

    val focalItem = items.firstOrNull { item ->
        val itemStart = item.offset
        val itemEnd = item.offset + item.size
        focalY in itemStart..itemEnd
    }

    if (focalItem != null) {
        return PortfolioSection.fromIndex(focalItem.index)
    }

    val mostVisible = items.maxByOrNull { item ->
        (minOf(item.offset + item.size, viewportEnd) - maxOf(item.offset, viewportStart)).coerceAtLeast(0)
    } ?: items.first()

    return PortfolioSection.fromIndex(mostVisible.index)
}
