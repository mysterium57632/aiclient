package de.paull.gui

import de.paull.gui.components.Background
import de.paull.gui.components.Chats
import de.paull.gui.components.Screenshot
import de.paull.gui.components.Stats
import de.paull.gui.components.TerminalEmulator
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
        val COLOR_ELEMENT = Color(0, 0, 0, 180)
    }

    private val elements: MutableList<Drawable> = mutableListOf()
    private var render = true
    private var renderer: Thread? = null

    val prompt: TerminalEmulator
    val shot: Screenshot
    val bridge: Bridge
    private val input: FrameKeyListener
    val stats: Stats
    val chats: Chats

    init {
        size = Frame.SIZE
        background = Color(0, 0, 0, 0)

        shot = Screenshot(this)
        prompt = TerminalEmulator(this)
        bridge = Bridge(this)
        input = FrameKeyListener(this)
        stats = Stats(this)
        addKeyListener(input)

        elements.add(Background(this))
        elements.add(shot)
        elements.add(prompt)
        elements.add(stats)
        elements.add(Chats(this))
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
    }

    fun stop() {
        for (e in elements) e.stop()
        if (renderer == null) return
        render = false
    }

    private fun drawFrame() {
        val g2d = bufferStrategy.drawGraphics as Graphics2D

        g2d.font = FONT

        elements[0].draw(g2d)

        if (shot.rectangle == null) {
            elements[1].draw(g2d)
            elements[2].draw(g2d)
            elements[3].draw(g2d)
            elements[4].draw(g2d)
        }

        g2d.dispose()
        bufferStrategy.show()
        Toolkit.getDefaultToolkit().sync()
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
            drawFrame()
            time = System.currentTimeMillis() - time
            time = max(15 - time, 0) // should be ruffly 60 FPS
            if (time > 0) sleep(time)
        }
        this.renderer = null
    }
}