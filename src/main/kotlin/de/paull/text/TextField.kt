package de.paull.text

import de.paull.gui.Master
import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage

class TextField(val width: Int) {

    companion object {
        const val LINE_HEIGHT = 24
        const val MESSAGE_SPACING = 0
        private const val FAKE_SPACE = 10
    }

    private val messages: MutableList<Message> = mutableListOf()
    var lastAiMessage: String? = null
        private set

    private var image: BufferedImage? = null

    val displayName: String
        get() {
            try {
                return messages[0].display
            } catch (_: IndexOutOfBoundsException) {}
            return "Unknown"
        }

    /* FUNCTIONS */

    fun draw(x: Int, y: Int, g2d: Graphics2D): Int {
        g2d.setRenderingHints(Master.IMAGE_RENDER_HINTS)
        val img = image ?: return y + LINE_HEIGHT
        g2d.drawImage(img, null, x, y)
        return y + img.height + LINE_HEIGHT - FAKE_SPACE
    }

    fun add(s: String) {
        inThread {
            messages.add(Message(s, width = width))
            render()
        }
    }

    fun addAI(s: String) {
        add(s)
        lastAiMessage = s.trim()
    }

    fun clear() {
        messages.clear()
        image = null
        inThread { render() }
    }

    private fun render() {
        val height = getHeight()
        if (height == 0) {
            image = null
            return
        }
        // Create Image
        val img = BufferedImage(width, height + FAKE_SPACE, BufferedImage.TYPE_INT_ARGB)
        val g2d = img.createGraphics() as Graphics2D
        g2d.color = Master.COLOR_ELEMENT
        g2d.fillRect(0, 0, img.width, img.height)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)
        g2d.color = Color.WHITE
        g2d.font = Master.FONT
        // Draw text to Image
        var y = LINE_HEIGHT
        val messages = this.messages.toList()
        for (m in messages) y = m.draw(g2d, y)
        this.image = img
    }

    private fun getHeight(): Int {
        var h = 0
        for (m in messages) h += m.getHeight()
        return h
    }

    private fun inThread(function: () -> Unit) {
        Thread {
            function()
        }.start()
    }
}