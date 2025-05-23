package de.paull.text

import de.paull.gui.Master
import de.paull.gui.components.Chats
import de.paull.text.elements.LaTexElement
import de.paull.text.elements.TextElement
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.image.BufferedImage
import java.util.regex.Matcher
import java.util.regex.Pattern

class Message(raw: String, private val fromAI: Boolean = false, private val width: Int) {

    companion object {
        private val latexInlinePattern: Pattern = Pattern.compile("\\\\[(](.*?[^\\\\])\\\\[)]", Pattern.DOTALL)
    }

    private val rawText: String = raw.trim()
    private val lines: MutableList<Line> = mutableListOf()
    var display: String = ""
         get() {
            if (field.isEmpty())
                field = getDisplayName()
            return field
        }

    init {
        val t = "> $rawText"
        val lines = t.split("\n")
        var latexOn = false
        for (i in lines.indices) {
            val l = lines[i]
            if (l.trim() == "\\[") {
                latexOn = true
                this.lines.add(Line.EMPTY_LINE)
                continue
            } else if (l.trim() == "\\]") {
                latexOn = false
                this.lines.add(Line.EMPTY_LINE)
                continue
            }

            if (latexOn) {
                val line = Line(width)
                line.add(LaTexElement(l.trim(), false))
                this.lines.add(line)
                continue
            }

            if (l.trim().isEmpty()) continue
            createLine(l.trimEnd())
        }
    }

    fun getHeight(): Int = lines.size * TextField.LINE_HEIGHT + TextField.MESSAGE_SPACING

    fun draw(g2d: Graphics2D, yy: Int): Int {
        var y = yy
        for (l in lines) y = l.draw(g2d, y)
        return y + TextField.MESSAGE_SPACING
    }

    private fun createLine(s: String) {
        var currentText = s
        var con: Boolean
        var l = Line(width)

        fun addElement(e: Element) {
            val ele = l.add(e) ?: return
            lines.add(l)
            l = Line(width)
            addElement(ele)
        }

        do {
            con = false
            val (point, latex) = nextLatex(currentText)
            if (point == null) {
                addElement(TextElement(currentText))
                continue
            }
            val t = currentText.substring(0, point.x)
            addElement(TextElement(t))
            currentText = if (point.y >= currentText.length) ""
                else currentText.substring(point.y)
            addElement(LaTexElement(latex))
            if (currentText.trim().isNotEmpty()) con = true
        } while (con)

        if (l.isEmpty()) return
        lines.add(l)
    }

    private fun nextLatex(input: String): Pair<Point?, String> {
        try {
            val matcher: Matcher = latexInlinePattern.matcher(input)
            if (!matcher.find()) return Pair(null, "")
            val latex: String = matcher.group(1)
            return Pair(Point(matcher.start(), matcher.end()), latex)
        } catch (_: IllegalStateException) {
            return Pair(null, "")
        }
    }

    private fun getDisplayName(): String {
        if (rawText.isEmpty()) return ""
        val dummy: Graphics2D = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics()
        val font = Master.FONT
        val metrics: FontMetrics = dummy.getFontMetrics(font)
        val maxWidth = Chats.WIDTH - 10 - metrics.stringWidth("...")
        if (metrics.stringWidth(rawText) <= maxWidth) return rawText
        var result = ""
        for (i in rawText.indices) {
            val substr = rawText.substring(0, i + 1)
            if (metrics.stringWidth(substr) > maxWidth) {
                return "${rawText.substring(0, i)}..."
            }
            result = substr
        }
        return "$result..."
    }

    abstract class Element {
        var width: Int = 0
            protected set

        abstract fun draw(g2d: Graphics2D, x: Int, y: Int): Int
    }
}