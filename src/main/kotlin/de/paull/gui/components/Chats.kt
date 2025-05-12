package de.paull.gui.components

import de.paull.gui.Drawable
import de.paull.gui.Master
import de.paull.text.TextField
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.GeneralPath

class Chats(master : Master) : Drawable(master, y = 100, width = 200, height = 800) {

    companion object {
        private val chats: MutableList<TextField> = mutableListOf()
        var currentChat: TextField? = null
    }

    private val PATH_BORDER: GeneralPath?
    private val PATH_FILL: GeneralPath?

    init {
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
        currentChat = text
        return text
    }

    override fun draw(g2d: Graphics2D) {
        // Draw Background
        g2d.color = Master.COLOR_ELEMENT
        g2d.fill(PATH_FILL)
        g2d.color = Color.GRAY
        g2d.stroke = BasicStroke(2f)
        g2d.draw(PATH_BORDER)
    }

    override fun start() {
    }

    override fun stop() {
    }
}