package de.paull.gui

import de.paull.gui.components.Background
import de.paull.gui.components.Chats
import de.paull.gui.components.Screenshot
import de.paull.gui.components.Stats
import de.paull.gui.components.TerminalEmulator
import de.paull.keys.FrameKeyListener
import java.awt.*
import kotlin.math.max

class Master : Canvas(), Runnable {

    companion object {
        const val FONT_SIZE = 18
        val FONT = Font("Monospaced", Font.PLAIN, FONT_SIZE)
        val FONT_BOLD = Font("Monospaced", Font.BOLD, FONT_SIZE)
        val FONT_ITALIC = Font("Monospaced", Font.ITALIC, FONT_SIZE)
        val FONT_HEADER = Font("Monospaced", Font.PLAIN, 30)

        val COLOR_BACKGROUND = Color(0, 0, 0, 120)
        val COLOR_ELEMENT = Color(0, 0, 0, 200)
        val COLOR_HIGHLIGHT = Color(50, 50, 50, 200)

        val TEXT_RENDER_HINTS = RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON).apply {
            put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        }

        val IMAGE_RENDER_HINTS = RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF).apply {
            put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED)
            put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
        }

        val FAST_RENDER_HINTS = RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF).apply {
            put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED)
        }
    }

    private val elements: MutableList<Drawable> = mutableListOf()
    private var render = true
    private var renderer: Thread? = null

    val back = Background(this)
    val prompt: TerminalEmulator
    val shot: Screenshot
    val input: FrameKeyListener
    val stats: Stats
    val chats: Chats = Chats(this)

    init {
        size = Frame.SIZE
        focusTraversalKeysEnabled = false // So TAB gets recognized by the KeyListener
        background = Color(0, 0, 0, 0)

        shot = Screenshot(this)
        prompt = TerminalEmulator(this)
        input = FrameKeyListener(this)
        stats = Stats(this)
        addKeyListener(input)
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
        for (e in elements) {
            e.start()
        }
    }

    fun stop() {
        for (e in elements) e.stop()
        if (renderer == null) return
        render = false
    }

    private fun drawFrame() {
        val g2d = bufferStrategy.drawGraphics as Graphics2D

        g2d.font = FONT

        back.draw(g2d)
        if (shot.rectangle == null) {
            prompt.draw(g2d)
            chats.draw(g2d)
            shot.draw(g2d)
            stats.draw(g2d)
        }

        g2d.dispose()
        if (Frame.FRAME == null || !Frame.FRAME!!.isVisible) return
        bufferStrategy.show()
        Toolkit.getDefaultToolkit().sync()
    }
    
    /**
     * Frame update draw loop
     */
    override fun run() {
        fun sleep(t: Long) {
            try {
                Thread.sleep(t)
            } catch (_: Exception) {}
        }

        while (render) {
            var time = System.nanoTime()
            drawFrame()
            time = System.nanoTime() - time
            stats.addFPS((1_000_000_000.0 / time.toDouble()).toInt())
            time = max(15 - time, 0) // should be ruffly 60 FPS
            if (time > 0) sleep(time)
        }
        this.renderer = null
    }
}