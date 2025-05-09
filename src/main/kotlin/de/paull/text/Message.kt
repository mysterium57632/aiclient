package de.paull.text

import de.paull.text.elements.LaTexElement
import de.paull.text.elements.TextElement
import java.awt.Graphics2D
import java.awt.Point
import java.util.regex.Matcher
import java.util.regex.Pattern

class Message(raw: String, private val fromAI: Boolean = false) {

    companion object {
        val WIDTH = TextHandler.WIDTH
        private val latexInlinePattern: Pattern = Pattern.compile("\\\\[(](.*?[^\\\\])\\\\[)]", Pattern.DOTALL)
    }

    private val rawText: String = raw.trim()
    private val lines: MutableList<Line> = mutableListOf()

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
                val line = Line()
                line.add(LaTexElement(l.trim(), false))
                this.lines.add(line)
                continue
            }

            if (l.trim().isEmpty()) continue
            createLine(l.trimEnd())
        }
    }

    fun getHeight(): Int = lines.size * TextHandler.LINE_HEIGHT + TextHandler.MESSAGE_SPACING

    fun draw(g2d: Graphics2D, yy: Int): Int {
        var y = yy
        for (l in lines) y = l.draw(g2d, y)
        return y + TextHandler.MESSAGE_SPACING
    }

    private fun createLine(s: String) {
        var currentText = s
        var con: Boolean
        var l = Line()

        fun addElement(e: Element) {
            val ele = l.add(e) ?: return
            lines.add(l)
            l = Line()
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

    abstract class Element {
        var width: Int = 0
            protected set

        abstract fun draw(g2d: Graphics2D, x: Int, y: Int): Int
    }
}