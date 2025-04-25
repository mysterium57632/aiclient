package de.paull.gui.components

import de.paull.gui.Frame
import de.paull.gui.Master
import java.awt.Graphics2D

class Stats(master: Master) : Master.Drawable(master) {

    companion object {
        private var tokens: Int = 0
    }

    private var cur: Int = tokens

    init {
        x = Frame.SIZE.width - 120
        y = 20
    }

    fun update(newTokens: Int) {
        tokens += newTokens
    }

    override fun draw(g2d: Graphics2D) {
        g2d.drawString("$cur Tokens", x, y)
        if (cur < tokens) cur += 1
    }

    override fun start() {}

    override fun stop() {}
}