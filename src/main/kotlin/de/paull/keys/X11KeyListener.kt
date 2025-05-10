package de.paull.keys

import de.paull.gui.Frame

class X11KeyListener : Runnable {

    companion object {
        var frame: Frame? = null
        var check = true
    }

    init {
        Thread(this).start()
    }

    override fun run() {
        start()
    }

    fun start() {
        val x11 = X11.INSTANCE
        val display = x11.XOpenDisplay(null)

        if (display == null) {
            println("Unable to open X display")
            return
        }

        val root = x11.XDefaultRootWindow(display)

        val modifiers = X11.Mod4Mask // Super key (Windows key)
        val escapeKey = 0xFF1B // XK_Escape
        val keycode = x11.XKeysymToKeycode(display, escapeKey)

        x11.XGrabKey(display, keycode, modifiers, root, 1, 1, 1)

        val event = X11.XEvent()

        while (check) {
            x11.XNextEvent(display, event)
            if (event.type == X11.KeyPress) {
                println("Hotkey pressed! Launching program...")
                if (Frame.FRAME != null) return
                Frame.FRAME = Frame()
            }
        }
    }
}