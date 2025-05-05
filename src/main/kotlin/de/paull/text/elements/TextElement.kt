package de.paull.text

import de.paull.gui.Master

import java.awt.Graphics2D
import java.awt.image.BufferedImage

class TextElement(private var raw: String) : Message.Element() {

    companion object {
        private val dummyG2D = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics()
    }

    private val g2d = dummyG2D

    init {
        width = calcWidth(raw)
    }

    override fun draw(g2d: Graphics2D, x: Int, y: Int): Int {
        g2d.drawString(raw, x, y)
        return x + calcWidth(raw)
    }

    fun split(allowedLength: Int, forceSplit: Boolean = false): Pair<TextElement?, TextElement?> {
        val str = lineWrap(allowedLength)
        if (str != null)
            return Pair(this, TextElement(str))
        if (!forceSplit)
            return Pair(null, this)
        val index = splitWord(allowedLength)
        val otherString = raw.substring(index).trim()
        raw = raw.substring(0, index - 1).trim()
        return Pair(this, TextElement(otherString))
    }

    private fun calcWidth(str: String): Int {
        g2d.font = Master.FONT
        val m = g2d.fontMetrics
        return m.stringWidth(str)
    }

    private fun lineWrap(max: Int): String? {
        var text = raw
        while (text.isNotEmpty() && calcWidth(text) >= max) {
            val index = text.indexOfLast { it.isWhitespace() }
            text =
                if (index == -1) ""
                else text.substring(0, index).trimEnd()
        }
        if (text.isEmpty()) return null
        val otherString = raw.substring(text.length).trim()
        raw = text
        return otherString
    }

    private fun splitWord(max: Int): Int {
        var currentWidth = 0
        g2d.font = Master.FONT
        for (i in raw.indices) {
            currentWidth += g2d.fontMetrics.charWidth(raw[i])
            if (currentWidth >= max)
                return i
        }
        return 0
    }
}