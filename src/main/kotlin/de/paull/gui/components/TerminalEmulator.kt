package de.paull.gui.components

import de.paull.gui.Drawable
import de.paull.gui.Master
import de.paull.text.TextField
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.Timer

class TerminalEmulator(master: Master) : Drawable(master, 50, 50, 1000) {

    companion object {
        val SONDERZEICHEN = setOf('.', ',', ';', ':', '?', '!', '-', '_', '+', '=', '"', '\'', '(', ')', '[', ']', '{', '}')
    }

    var typed: String = ""
    val chats = master.chats

    private val startX = x
    private val startY = y

    private var cursorTimer: Timer? = null
    private var drawCursor = true

    private var thinkTimer: Timer? = null
    private var drawThink: String? = null

    override fun draw(g2d: Graphics2D) {
        g2d.font = Master.FONT
        g2d.color = Color.WHITE
        var y = startY
        val text = Chats.currentChat
        if (text != null) y = text.draw(startX, y, g2d)

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)

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

    private fun stopCursor() {
        cursorTimer?.stop() ?: return
        cursorTimer = null
    }

    fun stopThinkTimer() {
        thinkTimer?.stop() ?: return
        thinkTimer = null
        drawThink = null
    }

    fun sendMessage(str: String): TextField {
        var current = Chats.currentChat
        if (current == null) current = chats.addChat(TextField(width - 50))
        current.add(str)
        resetInput()
        return current
    }

    fun clearTerminal() {
        
    }

    fun resetInput() = run { typed = "" }

    private fun drawCursor(g2d: Graphics2D, yy: Int) {
        val y = yy - TextField.LINE_HEIGHT + 1
        val h = 14
        val x = g2d.fontMetrics.stringWidth("$ $typed") + startX
        g2d.fillRect(x + 1, y - h, 5, h)
    }

    private fun drawLine(g2d: Graphics2D, str: String, y: Int): Int {
        g2d.drawString(str, startX, y)
        return y + TextField.LINE_HEIGHT
    }
}
