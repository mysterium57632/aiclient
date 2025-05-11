package de.paull.gui.components

import de.paull.gui.Drawable
import de.paull.gui.Frame
import de.paull.gui.Master
import java.awt.Graphics2D

class Stats(master: Master) : Drawable(master) {

    companion object {
        var tokens: Int = 0
            set(value) {
                field += value
            }
    }

    private var cur: Int = tokens

    init {
        x = Frame.SIZE.width - 120
        y = 20
    }

    override fun draw(g2d: Graphics2D) {
        g2d.drawString("$cur Tokens", x, y)
        if (cur < tokens) cur += 1
        g2d.font = Master.FONT_HEADER
        g2d.drawString("ai-client", 50, 75)
    }

    override fun start() {}
    override fun stop() {}
}