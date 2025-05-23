package de.paull.gui.components

import de.paull.gui.Drawable
import de.paull.gui.Master
import de.paull.text.TextField
import de.paull.text.TextField.Companion.LINE_HEIGHT
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.Timer

class TerminalEmulator(master: Master) : Drawable(master, 220, 100, 800, height = 900) {

    companion object {
        val SONDERZEICHEN = setOf('.', ',', ';', ':', '?', '!', '-', '_', '+', '=', '"', '\'', '(', ')',
            '[', ']', '{', '}', '^', '&', '*', '\\', '/', '%', '$', '@', '#')
    }

    var typed: String = ""
    val chats = master.chats

    private val startX = x + 10
    private val startY = y + 5

    private var cursorTimer: Timer? = null
    private var drawCursor = true

    private var thinkTimer: Timer? = null
    private var drawThink: String? = null

    override fun draw(g2d: Graphics2D) {
        drawBackground(g2d)

        g2d.font = Master.FONT
        g2d.color = Color.WHITE

        var y = startY
        val text = chats.currentChat
        if (text != null) y = text.draw(startX, y, g2d)
        else y += LINE_HEIGHT

        g2d.setRenderingHints(Master.TEXT_RENDER_HINTS)
        g2d.color = Color.WHITE
        val dt = drawThink
        if (dt != null) {
            g2d.color = Color.GRAY
            y = drawLine(g2d, dt, y)
            g2d.color = Color.WHITE
        }
        y = drawLine(g2d, "$ $typed", y)
        if (drawCursor) drawCursor(g2d, y)
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

    fun stopCursor() {
        drawCursor = false
        cursorTimer?.stop() ?: return
        cursorTimer = null
    }

    fun stopThinkTimer() {
        thinkTimer?.stop() ?: return
        thinkTimer = null
        drawThink = null
    }

    fun sendMessage(str: String): TextField {
        var current = chats.currentChat
        if (current == null) current = chats.addChat(TextField(width - 50))
        current.add(str)
        resetInput()
        chats.requestFocus(current)
        return current
    }

    fun clearTerminal() {
        resetInput()
        chats.highlightIndex = -1
    }

    fun resetInput() = run { typed = "" }

    private fun drawCursor(g2d: Graphics2D, yy: Int) {
        val y = yy - LINE_HEIGHT + 1
        val h = 14
        val x = g2d.fontMetrics.stringWidth("$ $typed") + startX
        g2d.fillRect(x + 1, y - h, 5, h)
    }

    private fun drawLine(g2d: Graphics2D, str: String, y: Int): Int {
        g2d.drawString(str, startX, y)
        return y + LINE_HEIGHT
    }

    private fun drawBackground(g2d: Graphics2D) {
        g2d.setRenderingHints(Master.FAST_RENDER_HINTS)
        g2d.color = Master.COLOR_ELEMENT
        g2d.fillRoundRect(x, y, width, height, 8, 8)

        //g2d.setRenderingHints(Master.TEXT_RENDER_HINTS)
        g2d.color = Color.GRAY
        g2d.stroke = BasicStroke(2f)
        g2d.drawRoundRect(x, y, width, height, 8, 8)
    }
}
