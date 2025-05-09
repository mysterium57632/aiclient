package de.paull.text

import de.paull.gui.Master
import de.paull.gui.components.Background
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage

class TextHandler(width: Int) {

    companion object {
        const val LINE_HEIGHT = 24
        const val MESSAGE_SPACING = 0
        var WIDTH = -1
        private const val FAKE_SPACE = 10
    }

    private val messages: MutableList<Message> = mutableListOf()
    var lastAiMessage: String? = null
        private set
    private var image: BufferedImage? = null

    init {
        WIDTH = width
    }

    fun draw(x: Int, y: Int, g2d: Graphics2D): Int {
        val img = image ?: return y + LINE_HEIGHT
        g2d.drawImage(img, null, x, y)
        return y + img.height + LINE_HEIGHT - FAKE_SPACE
    }

    fun add(s: String) {
        inThread {
            messages.add(Message(s))
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
        val img = BufferedImage(WIDTH, height + FAKE_SPACE, BufferedImage.TYPE_INT_ARGB)
        val g2d = img.createGraphics() as Graphics2D
        g2d.color = Background.COLOR
        g2d.fillRect(0, 0, img.width, img.height)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)
        g2d.color = Color.WHITE
        g2d.font = Master.FONT
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