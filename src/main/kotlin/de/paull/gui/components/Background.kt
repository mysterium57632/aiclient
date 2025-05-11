package de.paull.gui.components

import de.paull.gui.Drawable
import de.paull.gui.Master
import java.awt.*

class Background(master: Master) : Drawable(master) {

    companion object {
    }

    override fun draw(g2d: Graphics2D) {
        g2d.composite = AlphaComposite.Src
        g2d.color = Master.COLOR_BACKGROUND
        g2d.fillRect(0, 0, width, height)

        master.shot.rectangle?.let {
            g2d.composite = AlphaComposite.Clear
            g2d.fillRect(it.x, it.y, it.width, it.height)

            g2d.composite = AlphaComposite.SrcOver
            g2d.stroke = BasicStroke(2f)
            g2d.color = Color.WHITE
            g2d.drawRect(it.x, it.y, it.width, it.height)
        }
    }

    override fun start() {}
    override fun stop() {}
}