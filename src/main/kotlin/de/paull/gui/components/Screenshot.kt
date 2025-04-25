package de.paull.gui.components

import de.paull.gui.Frame
import de.paull.gui.Master
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

class Screenshot(master: Master) : Master.Drawable(master, Frame.SIZE.width - 350, 50, 300) {

    var rawImage: String? = null
        private set
    private var scaledImage: BufferedImage? = null

    override fun draw(g2d: Graphics2D) {
        val img = scaledImage ?: return
        g2d.drawString("Screenshot added", x, y)
        g2d.renderingHints.clear()
        g2d.drawImage(img, null, x, y + 18)
        g2d.stroke = BasicStroke(1f)
        g2d.drawRect(x, y + 20, img.width, img.height)
    }

    override fun start() {}

    override fun stop() {}

    fun setImage(img: BufferedImage) {
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(img, "png", outputStream)
        val imageBytes = outputStream.toByteArray()
        rawImage = Base64.getEncoder().encodeToString(imageBytes)

        val w = width
        val originalWidth: Int = img.width
        val originalHeight: Int = img.height

        val targetHeight: Int = (w * originalHeight) / originalWidth
        val scaledImage = BufferedImage(w, targetHeight, BufferedImage.TYPE_INT_ARGB)
        val g2d = scaledImage.createGraphics()

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g2d.drawImage(img, 0, 0, w, targetHeight, null)
        g2d.dispose()

        this.scaledImage = scaledImage
    }

    fun clearImage() {
        scaledImage = null
        rawImage = null
    }
}