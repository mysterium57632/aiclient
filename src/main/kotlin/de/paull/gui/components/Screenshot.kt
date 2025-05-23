package de.paull.gui.components

import de.paull.gui.Drawable
import de.paull.gui.Frame
import de.paull.gui.Master
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO

class Screenshot(master: Master) : Drawable(master, Frame.SIZE.width - 350, 50, 300) {

    @Volatile
    var rectangle: Rectangle? = null
        private set

    var rawImage: String? = null
        private set

    private var scaledImage: BufferedImage? = null

    init {
        val m = MLis(this)
        master.addMouseListener(m)
        master.addMouseMotionListener(m)
    }

    override fun draw(g2d: Graphics2D) {
        val img = scaledImage ?: return
        g2d.drawString("Screenshot added", x, y)
        g2d.renderingHints.clear()
        g2d.drawImage(img, null, x, y + 18)
        g2d.color = Color.WHITE
        g2d.stroke = BasicStroke(1f)
        g2d.drawRect(x, y + 18, img.width, img.height)
    }

    override fun start() {}

    override fun stop() {}

    /**
     * This method is called on a screenshot Event.
     * It will create the Image by running the gnome-screenshot program
     * and loads the resulting image into the program for further processing.
     * The sub image will be cut to the corresponding size and a scaled version
     * for display will be rendered.
     */
    fun onScreenshot() {
        val r: Rectangle = rectangle ?: return
        // take screenshot
        val path = "screenshot.png"
        val process = ProcessBuilder("gnome-screenshot", "-f", path)
            .inheritIO()
            .start()
        process.waitFor()
        var img = ImageIO.read(File(path)) as BufferedImage
        // create Subimage
        img = img.getSubimage(r.x, r.y + Frame.TOP_BAR_HEIGHT, r.width, r.height)
        // Encode Image for ChatGPT usage
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(img, "png", outputStream)
        val imageBytes = outputStream.toByteArray()
        rawImage = Base64.getEncoder().encodeToString(imageBytes)
        // scale Image to size for display
        val w = width
        val originalWidth: Int = img.width
        val originalHeight: Int = img.height
        val targetHeight: Int = (w * originalHeight) / originalWidth
        val scaledImage = BufferedImage(w, targetHeight, BufferedImage.TYPE_INT_ARGB)
        val g2d = scaledImage.createGraphics()
        g2d.setRenderingHints(Master.IMAGE_RENDER_HINTS)
        g2d.drawImage(img, 0, 0, w, targetHeight, null)
        g2d.dispose()
        this.scaledImage = scaledImage
    }

    fun clearImage() {
        scaledImage = null
        rawImage = null
    }

    /**
     * Mouse listener for making screenshots on Mouse drag
     */
    private class MLis(private val shot: Screenshot) : MouseAdapter(), MouseMotionListener {

        private var pressed = false
        private var start = Point(0, 0)

        override fun mousePressed(e: MouseEvent?) {
            pressed = true
            start = Point(e?.x ?: 0, e?.y ?: 0)
            super.mousePressed(e)
        }

        override fun mouseReleased(e: MouseEvent?) {
            mouseDragged(e)
            pressed = false
            shot.onScreenshot()
            shot.rectangle = null
            super.mouseReleased(e)
        }

        override fun mouseDragged(e: MouseEvent?) {
            val r = Rectangle(start)
            r.add(Point(e?.x ?: 0, e?.y ?: 0))
            if (r.width != 0 && r.height != 0)
                shot.rectangle = r
            super.mouseDragged(e)
        }
    }
}