package de.paull

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.NativeLong
import com.sun.jna.PointerType
import com.sun.jna.Structure

interface X11 : Library {
    companion object {
        val INSTANCE: X11 = Native.load("X11", X11::class.java)

        const val ControlMask = 1 shl 2
        const val Mod1Mask = 1 shl 3  // Alt
        const val Mod4Mask = 1 shl 6  // Super
        const val KeyPress = 2
    }

    open class Display : PointerType()

    fun XOpenDisplay(displayName: String?): Display?
    fun XDefaultRootWindow(display: Display): Int
    fun XKeysymToKeycode(display: Display, keysym: Int): Int
    fun XGrabKey(
        display: Display, keycode: Int, modifiers: Int, grabWindow: Int,
        ownerEvents: Int, pointerMode: Int, keyboardMode: Int
    ): Int

    fun XSelectInput(display: Display, w: Int, eventMask: Int): Int
    fun XNextEvent(display: Display, event: XEvent)

    class XEvent : Structure() {
        @JvmField var type = 0
        @JvmField var serial = NativeLong(0)
        @JvmField var send_event = false
        @JvmField var display: Display? = null
        @JvmField var window = 0
        @JvmField var root = 0
        @JvmField var subwindow = 0
        @JvmField var time = 0
        @JvmField var x = 0
        @JvmField var y = 0
        @JvmField var x_root = 0
        @JvmField var y_root = 0
        @JvmField var state = 0
        @JvmField var keycode = 0
        @JvmField var same_screen = false

        override fun getFieldOrder(): List<String> {
            return listOf(
                "type", "serial", "send_event", "display", "window",
                "root", "subwindow", "time", "x", "y", "x_root", "y_root",
                "state", "keycode", "same_screen"
            )
        }
    }
}