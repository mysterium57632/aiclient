package de.paull.keys

import de.paull.gui.Frame
import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import java.util.logging.Level
import java.util.logging.Logger

/**
 * DEPRECATED: NOTIFY SERVER INSTEAD!
 *
 * Global Key Listener for the Project.
 *
 * @author paull
 * @deprecated Use NotifyServer instead!
 * @see de.paull.net.NotifyServer
 *
 * @constructor Creates a GlobalKeyListener for the Frame.
 */
class GlobalKeyListener : NativeKeyListener {

    private var altPressed: Boolean = false
    private var ctrlPressed: Boolean = false

    init {
        val logger: Logger = Logger.getLogger(GlobalScreen::class.java.getPackage().name)
        logger.level = Level.OFF
        logger.useParentHandlers = false

        GlobalScreen.registerNativeHook()
        GlobalScreen.addNativeKeyListener(this)
    }

    override fun nativeKeyTyped(p0: NativeKeyEvent?) {}

    override fun nativeKeyPressed(e: NativeKeyEvent?) {
        when (e?.keyCode) {
            NativeKeyEvent.VC_ALT -> {
                altPressed = true
            }
            NativeKeyEvent.VC_CONTROL -> {
                ctrlPressed = true
            }
            NativeKeyEvent.VC_BACKSPACE -> {
                if (altPressed && ctrlPressed) {
                    if (Frame.FRAME != null) return
                    Frame.FRAME = Frame()
                }
            }
        }
    }

    override fun nativeKeyReleased(e: NativeKeyEvent?) {
        when (e?.keyCode) {
            NativeKeyEvent.VC_ALT -> altPressed = false
            NativeKeyEvent.VC_CONTROL -> ctrlPressed = false
        }
    }
}