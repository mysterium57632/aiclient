package de.paull.text.elements

import de.paull.gui.Master
import de.paull.text.Message
import de.paull.text.TextField
import org.scilab.forge.jlatexmath.TeXFormula
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.swing.JLabel

class LaTexElement(private val raw: String, private val inline: Boolean = true) : Message.Element() {

    private val image: BufferedImage

    init {
        image = renderLatex()
        width = image.width
    }

    override fun draw(g2d: Graphics2D, x: Int, y: Int): Int {
        val yy = if (inline) y - 19 else y - TextField.LINE_HEIGHT + 2
        g2d.drawImage(image, null, x, yy)

        val c: Color = g2d.color
        g2d.color = Color.RED
        g2d.drawRect(x, yy, image.width, image.height)
        g2d.color = Color.BLUE
        g2d.drawLine(x - 5, yy + image.height / 2, x + image.width + 10, image.height / 2)
        g2d.color = c

        return x + image.width
    }

    private fun renderLatex(): BufferedImage {
        val formula = TeXFormula(raw)
        val icon = formula.createTeXIcon(TeXFormula.BOLD, Master.FONT_SIZE.toFloat() + 5)
        val image = BufferedImage(icon.iconWidth, icon.iconHeight, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)
        g2d.color = Color(255, 255, 255, 0)
        g2d.fillRect(0, 0, image.width, image.height)
        g2d.color = Color.WHITE
        icon.setForeground(Color.WHITE)
        icon.paintIcon(JLabel(), g2d, 0, 0)
        g2d.dispose()
        return image
    }

}