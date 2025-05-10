package de.paull.gui

import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.SwingUtilities

fun getHeight() : Int {
    val gd = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
    val config = gd.defaultConfiguration
    val insets = Toolkit.getDefaultToolkit().getScreenInsets(config)
    return insets.top
}

class Frame : JFrame() {

    companion object {
        var FRAME: Frame? = null
        private val screen = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds
        val SIZE: Dimension = Dimension(screen.width, screen.height)
        val TOP_BAR_HEIGHT = getHeight()
    }

    private var master: Master? = null

    init {
        isUndecorated = true
        background = Color(0, 0, 0, 0)
        location = Point(0, 0)
        size = SIZE
        isAlwaysOnTop = true
        isVisible = false
        defaultCloseOperation = HIDE_ON_CLOSE
        isFocusable = true
        addListener()

        activate()
    }

    fun activate() {
        if (isVisible) return
        isVisible = true

        //removeAll()
        val master = Master()
        add(master)
        this.master = master

        revalidate()
        validate()
        repaint()

        SwingUtilities.invokeLater {
            master.start()
            master.repaint()
        }
    }

    fun deactivate() {
        isVisible = false
        master?.stop()
        dispose()
        FRAME = null
    }

    private fun addListener() {
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                deactivate()
            }

            override fun windowClosed(e: WindowEvent) {}
            override fun windowDeactivated(e: WindowEvent) {}

            override fun windowIconified(e: WindowEvent) {
                deactivate()
            }
        })
    }
}