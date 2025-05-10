package de.paull.keys

import de.paull.gui.Frame
import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import java.util.logging.Level
import java.util.logging.Logger

class GlobalKeyListener : NativeKeyListener {

    private var altPressed: Boolean = false

    init {
        val logger: Logger = Logger.getLogger(GlobalScreen::class.java.getPackage().name)
        logger.level = Level.OFF
        logger.useParentHandlers = false
    }

    override fun nativeKeyTyped(p0: NativeKeyEvent?) {}

    override fun nativeKeyPressed(e: NativeKeyEvent?) {
        if (e?.keyCode == NativeKeyEvent.VC_ALT) {
            altPressed = true
        } else if (altPressed && e?.keyCode == 92) {
            if (Frame.FRAME != null) return
            Frame.FRAME = Frame()
        } else if (altPressed && e?.keyCode == 3675) {
            Frame.FRAME?.deactivate()
            Frame.FRAME = null
        }
    }

    override fun nativeKeyReleased(e: NativeKeyEvent?) {
        if (e?.keyCode == NativeKeyEvent.VC_ALT) {
            altPressed = false;
        }
    }
}