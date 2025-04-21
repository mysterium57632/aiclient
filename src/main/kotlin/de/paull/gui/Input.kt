package de.paull.gui

import de.paull.gui.components.TerminalEmulator
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.awt.event.KeyListener


class Input(private val bridge: Bridge,
            private val text: TerminalEmulator
) : KeyListener {

    override fun keyTyped(e: KeyEvent?) {
        val c = e?.keyChar ?: return
        if (c.code == KeyEvent.VK_BACK_SPACE) {
            if (text.typed.isNotEmpty()) {
                text.typed = text.typed.dropLast(1)
            }
        } else if (!e.isControlDown && c.isLetterOrDigit() || c.isWhitespace() || c in TerminalEmulator.SONDERZEICHEN) {
            text.typed += c
        }
    }

    override fun keyPressed(e: KeyEvent?) {
        if (e?.keyCode == KeyEvent.VK_ENTER) {
            val t = text.typed.trim()
            if (t == "clear")
                return text.clearTerminal()
            if (bridge.blocked) return
            text.resetInput()
            text.add(t)
            return bridge.onEnter(t)
        }
        if (e?.keyCode == KeyEvent.VK_V && e.isControlDown) {
            try {
                val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                val paste = clipboard.getData(DataFlavor.stringFlavor) as String
                text.typed += paste.trim()
                return
            } catch (_: Exception) {}
        }
        if (e?.keyCode == KeyEvent.VK_C && e.isControlDown) {
            try {
                val text = text.lastAwnser ?: return
                val stringSelection = StringSelection(text)
                val toolkit = Toolkit.getDefaultToolkit()
                val clipboard = toolkit.systemClipboard
                clipboard.setContents(stringSelection, null)
            } catch (_: Exception) {}
        }
    }

    override fun keyReleased(p0: KeyEvent?) {}
}