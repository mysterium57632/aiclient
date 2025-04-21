package de.paull.gui.components

import de.paull.gui.Master
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.Timer

class TerminalEmulator(master: Master) : Master.Drawable(master, 50, 50, 1000) {

    companion object {
        val SONDERZEICHEN = setOf('.', ',', ';', ':', '?', '!', '-', '_', '+', '=', '"', '\'', '(', ')', '[', ']', '{', '}')
    }

    private val lineHeight = 25
    private val startX = x + 50
    private val startY = y + 50
    private val maxWidth = width - 100
    private var index: Int = 0

    private val lines: MutableList<String> = mutableListOf()
    var lastAwnser: String? = null
        private set
    var typed: String = ""

    private var cursorTimer: Timer? = null
    private var drawCursor = true

    private var thinkTimer: Timer? = null
    private var drawThink: String? = null

    private var g2d: Graphics2D

    init {
        val dummyImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        g2d = dummyImage.createGraphics()
        g2d.font = Master.FONT
    }

    override fun draw(g2d: Graphics2D) {
        g2d.color = Color.WHITE
        index = 0
        for (l in lines) {
            drawLine(g2d, l.trim())
        }
        val dt = drawThink
        if (dt != null) {
            g2d.color = Color.GRAY
            drawLine(g2d, dt)
            g2d.color = Color.WHITE
        }
        drawLine(g2d, "$ $typed")
        if (drawCursor) drawCursor(g2d)
    }

    override fun start() {
        val t = Timer(500) {
            drawCursor = drawCursor.not()
        }
        t.start()
        cursorTimer = t
    }

    fun startThinkTimer() {
        val list = listOf(".",  "..", "...")
        var i = 0
        val t = Timer(500) {
            if (i % 3 == 0) i = 0
            drawThink = list[i]
            i++
        }
        t.initialDelay = 0
        t.start()
        thinkTimer = t
    }

    override fun stop() {
        stopCursor()
        stopThinkTimer()
    }

    private fun stopCursor() {
        cursorTimer?.stop() ?: return
        cursorTimer = null
    }

    fun stopThinkTimer() {
        thinkTimer?.stop() ?: return
        thinkTimer = null
        drawThink = null
    }

    fun add(str: String) {
        lineWrap("> $str")
    }

    fun addAI(str: String) {
        add(str)
        lastAwnser = str.trim()
    }

    fun clearTerminal() = lines.clear()

    fun resetInput() = run { typed = "" }

    private fun drawCursor(g2d: Graphics2D) {
        val h = 14
        val index = index - 1
        val x = g2d.fontMetrics.stringWidth("$ $typed") + startX
        val y = startY + index * lineHeight
        g2d.fillRect(x + 1, y - h, 5, h)
    }

    private fun drawLine(g2d: Graphics2D, str: String) {
        g2d.drawString(str, startX, startY + index * lineHeight)
        index++
    }

    private fun lineWrap(raw: String) {
        val m = g2d.fontMetrics
        if (m.stringWidth(raw.replace("\\s+", " ")) < maxWidth) {
            lines.add(raw)
            return
        }

        fun check(line: String): Boolean = m.stringWidth(line) < maxWidth
        fun String.splitAt(index: Int): Pair<String, String> =
            this.substring(0, index) to this.substring(index)

        var line = ""
        for (word in raw.split(" ")) {
            val testLine = "$line $word"
            if (check(testLine)) {
                line = "$line $word"
                continue
            }
            if (line.isNotEmpty()) {
                lines.add(line)
                line = word
            }
            while (!check(line)) {
                val i = getWrapIndex(line)
                val (first, sec) = line.splitAt(i)
                lines.add(first)
                line = sec
            }
        }
        if (line.isNotEmpty()) lines.add(line)
    }

    private fun getWrapIndex(text: String): Int {
        var currentWidth = 0
        for (i in text.indices) {
            currentWidth += g2d.fontMetrics.charWidth(text[i])
            if (currentWidth >= maxWidth) {
                return i
            }
        }
        return 0
    }
}
