package de.paull.gui.components

import de.paull.gui.Master
import java.awt.*

class Background(master: Master) : Master.Drawable(master) {

    override fun draw(g2d: Graphics2D) {
        g2d.composite = AlphaComposite.Src
        g2d.color = Color(0, 0, 0, 180)
        g2d.fillRect(0, 0, width, height)

        master.rectangle?.let {
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