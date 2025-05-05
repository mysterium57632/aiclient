package de.paull.text

import de.paull.text.Message.Element
import de.paull.text.elements.TextElement
import java.awt.Graphics2D

class Line {

    companion object {
        private var MAX_WIDTH = Message.WIDTH
        val EMPTY_LINE = Line()
    }

    private val elements: MutableList<Element> = mutableListOf()
    private var size: Int = 0

    fun add(e: Element) : Element? {
        if (size + e.width < MAX_WIDTH) {
            elements.add(e)
            calcSize()
            return null
        }

        if (e !is TextElement)
            return e

        val maxLength = MAX_WIDTH - size
        val (short, new) = e.split(maxLength, isEmpty())
        if (short != null) elements.add(short)
        return new
    }

    fun isEmpty() : Boolean = elements.isEmpty()

    fun draw(g2d: Graphics2D, y: Int): Int {
        var x = 0
        for (e in elements) x = e.draw(g2d, x, y)
        return y + TextHandler.LINE_HEIGHT
    }

    private fun calcSize() {
        var s = 0
        for (e in elements)
            s += e.width
        size = s
    }
}