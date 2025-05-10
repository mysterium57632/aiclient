package de.paull.gui

import de.paull.gui.components.Background
import de.paull.gui.components.Screenshot
import de.paull.gui.components.Stats
import de.paull.gui.components.TerminalEmulator
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.max

class Master : Canvas(), Runnable {

    companion object {
        const val FONT_SIZE = 18
        val FONT = Font("Monospaced", Font.PLAIN, FONT_SIZE)
        val FONT_BOLD = Font("Monospaced", Font.BOLD, FONT_SIZE)
        val FONT_ITALIC = Font("Monospaced", Font.ITALIC, FONT_SIZE)
    }

    @Volatile
    var rectangle: Rectangle? = null
        private set
    private var redraw = true
    private val elements: MutableList<Drawable> = mutableListOf()
    private var render = true
    private var renderer: Thread? = null

    val prompt: TerminalEmulator
    val shot: Screenshot
    val bridge: Bridge
    private val input: Input
    val stats: Stats

    init {
        size = Frame.SIZE
        background = Color(0, 0, 0, 0)
        val m = MLis(this)
        addMouseListener(m)
        addMouseMotionListener(m)

        shot = Screenshot(this)
        prompt = TerminalEmulator(this)
        bridge = Bridge(this)
        input = Input(this)
        stats = Stats(this)

        addKeyListener(input)

        elements.add(Background(this))
        elements.add(shot)
        elements.add(prompt)
        elements.add(stats)
    }

    fun start() {
        this.requestFocus()
        createBufferStrategy(2)
        // Main render Thread
        this.render = true
        val r = Thread(this)
        r.start()
        this.renderer = r
        // Start other Elements
        for (e in elements)
            e.start()
        this.redraw = true
    }

    fun stop() {
        for (e in elements) e.stop()
        if (renderer == null) return
        render = false
    }

    override fun repaint() {
        redraw = true
    }

    private fun drawFrame() {
        val g2d = bufferStrategy.drawGraphics as Graphics2D

        g2d.font = FONT

        elements[0].draw(g2d)

        if (rectangle == null) {
            elements[1].draw(g2d)
            elements[2].draw(g2d)
            elements[3].draw(g2d)
        }

        g2d.dispose()
        bufferStrategy.show()
        Toolkit.getDefaultToolkit().sync()
    }

    fun onScreenshot() {
        val r: Rectangle = rectangle ?: return
        val path = "screenshot.png"
        val process = ProcessBuilder("gnome-screenshot", "-f", path)
            .inheritIO()
            .start()
        process.waitFor()
        var img = ImageIO.read(File(path)) as BufferedImage
        img = img.getSubimage(r.x, r.y + Frame.TOP_BAR_HEIGHT, r.width, r.height)
        shot.setImage(img)
    }

    private class MLis(private val rb: Master) : MouseAdapter(), MouseMotionListener {

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
            rb.onScreenshot()
            rb.rectangle = null
            super.mouseReleased(e)
        }

        override fun mouseDragged(e: MouseEvent?) {
            val r = Rectangle(start)
            r.add(Point(e?.x ?: 0, e?.y ?: 0))
            if (r.width != 0 && r.height != 0)
                rb.rectangle = r
            super.mouseDragged(e)
        }
    }

    /**
     * Frame update loop
     */
    override fun run() {
        fun sleep(t: Long) {
            try {
                Thread.sleep(t)
            } catch (_: Exception) {}
        }

        while (render) {
            var time = System.currentTimeMillis()
            if (redraw) drawFrame()
            time = System.currentTimeMillis() - time
            time = max(15 - time, 0)
            if (time > 0) sleep(time)
        }
        this.renderer = null
    }

    abstract class Drawable(
        protected val master: Master,
        protected var x: Int = 0,
        protected var y: Int = 0,
        protected var width: Int = Frame.SIZE.width,
        protected var height: Int = Frame.SIZE.height) {

        abstract fun draw(g2d: Graphics2D)
        abstract fun start()
        abstract fun stop()
    }
}