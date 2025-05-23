package de.paull.gui.components

import de.paull.gui.Drawable
import de.paull.gui.Master
import de.paull.text.TextField
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.GeneralPath
import java.util.concurrent.CopyOnWriteArrayList

class Chats(master : Master) : Drawable(master, y = 100, width = 200, height = 900) {

    companion object {
        // Need to use this kind of list to prevent ConcurrentModificationException
        private val chats: CopyOnWriteArrayList<TextField> = CopyOnWriteArrayList<TextField>()
        var WIDTH = 0
    }

    private val PATH_BORDER: GeneralPath?
    private val PATH_FILL: GeneralPath?
    var highlightIndex = -1
    val currentChat: TextField?
        get() {
            if (chats.isEmpty() || highlightIndex <= 0) return null
            return chats[highlightIndex - 1]
        }
    val listSize: Int
        get() = chats.size

    init {
        WIDTH = width
        val arc = 8
        val path = GeneralPath()

        path.moveTo(x.toFloat(), y.toFloat())
        path.lineTo((x + width - arc).toFloat(), y.toFloat())
        path.quadTo((x + width).toFloat(), y.toFloat(), (x + width).toFloat(), (y + arc).toFloat())
        path.lineTo((x + width).toFloat(), (y + height - arc).toFloat())
        path.quadTo((x + width).toFloat(), (y + height).toFloat(), (x + width - arc).toFloat(), (y + height).toFloat())
        path.lineTo(x.toDouble(), (y + height).toDouble());
        PATH_BORDER = path

        val fillPath = GeneralPath()
        fillPath.moveTo(x.toFloat(), y.toFloat())
        fillPath.lineTo((x + width - arc).toDouble(), y.toDouble())
        fillPath.quadTo((x + width).toDouble(), y.toDouble(), (x + width).toDouble(), (y + arc).toDouble())
        fillPath.lineTo((x + width).toDouble(), (y + height - arc).toDouble())
        fillPath.quadTo((x + width).toDouble(), (y + height).toDouble(), (x + width - arc).toDouble(), (y + height).toDouble())
        fillPath.lineTo(x.toFloat(), (y + height).toFloat())
        fillPath.lineTo(x.toFloat(), y.toFloat())
        fillPath.closePath()
        PATH_FILL = fillPath
    }

    fun addChat(text: TextField): TextField {
        chats.add(0, text)
        highlightIndex = 1
        return text
    }

    fun requestFocus(chat: TextField) {
        chats.remove(chat)
        chats.add(0, chat)
        highlightIndex = 1
    }

    override fun draw(g2d: Graphics2D) {
        // Draw Background
        g2d.setRenderingHints(Master.FAST_RENDER_HINTS)
        g2d.color = Master.COLOR_ELEMENT
        g2d.fill(PATH_FILL)
        g2d.clip(PATH_BORDER)

        val lineHeight = TextField.LINE_HEIGHT + 5

        // Apply Highlight
        fun highlight(index: Int) {
            val y = y + index * (lineHeight + 10)
            g2d.color = Master.COLOR_HIGHLIGHT
            g2d.fillRect(0,y, width, lineHeight + 10)
        }

        fun drawLine(y: Int) {
            val c = g2d.color
            g2d.color = Color.GRAY
            g2d.drawLine(x, y, x + width, y)
            g2d.color = c
        }

        val index = highlightIndex
        if (index != -1) highlight(index)

        g2d.setRenderingHints(Master.TEXT_RENDER_HINTS)

        // Draw Header
        var yy = y + lineHeight
        g2d.font = Master.FONT_BOLD
        g2d.color = Color.GRAY
        g2d.drawString("Chats - ${chats.size}", x + 5, yy)
        yy += 10

        // Draw Chats
        g2d.color = Color.WHITE
        g2d.font = Master.FONT
        drawLine(yy)

        val list = chats
        for (c in list) {
            yy += lineHeight
            g2d.drawString(c.displayName, x + 5, yy)
            yy += 10
            drawLine(yy)
        }

        // Draw Border
        g2d.setRenderingHints(Master.FAST_RENDER_HINTS)
        g2d.setClip(null)
        g2d.color = Color.GRAY
        g2d.stroke = BasicStroke(2f)
        g2d.draw(PATH_BORDER)
    }

    override fun start() {}

    override fun stop() {}
}