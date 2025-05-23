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

    private var currentTokens: Int = tokens
    private val fpsList = ArrayDeque<Int>()
    private var min: Int = 0

    init {
        x = Frame.SIZE.width - 120
        y = 20
    }

    fun addFPS(fps: Int) {
        while (fpsList.size > 10)
            fpsList.removeFirst()
        fpsList.add(fps)
        min = fpsList.average().toInt()
    }

    override fun draw(g2d: Graphics2D) {
        g2d.setRenderingHints(Master.TEXT_RENDER_HINTS)
        g2d.drawString("$currentTokens Tokens", x, y)
        g2d.drawString("$min fps", x - 120, y)
        if (currentTokens < tokens) currentTokens += 1
        g2d.font = Master.FONT_HEADER
        g2d.drawString("ai-client", 10, 50)
    }

    override fun start() {}
    override fun stop() {}
}