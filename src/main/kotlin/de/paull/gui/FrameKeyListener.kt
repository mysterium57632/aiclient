package de.paull.gui

import de.paull.gui.components.Chats
import de.paull.gui.components.TerminalEmulator
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class FrameKeyListener(master: Master) : KeyListener {

    private val bridge: Bridge = master.bridge
    private val promt: TerminalEmulator = master.prompt

    override fun keyTyped(e: KeyEvent?) {
        val c = e?.keyChar ?: return
        if (c.code == KeyEvent.VK_BACK_SPACE) {
            if (promt.typed.isNotEmpty()) {
                promt.typed = promt.typed.dropLast(1)
            }
        } else if (!e.isControlDown && c.isLetterOrDigit() || c.isWhitespace() || c in TerminalEmulator.SONDERZEICHEN) {
            promt.typed += c
        }
    }

    override fun keyPressed(e: KeyEvent?) {
        // On Enter: Send Message or clear terminal / create new Chat
        if (e?.keyCode == KeyEvent.VK_ENTER) {
            val t = promt.typed.trim()
            if (t == "clear")
                return promt.clearTerminal()
            if (bridge.blocked) return
            promt.sendMessage(t)
            return bridge.onEnter(t)
        }

        if (e?.keyCode == KeyEvent.VK_V && e.isControlDown) {
            try {
                val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                val paste = clipboard.getData(DataFlavor.stringFlavor) as String
                promt.typed += paste.trim()
                return
            } catch (_: Exception) {}
        }

        if (e?.keyCode == KeyEvent.VK_C && e.isControlDown) {
            try {
                val text = Chats.currentChat?.lastAiMessage ?: return
                val stringSelection = StringSelection(text)
                val toolkit = Toolkit.getDefaultToolkit()
                val clipboard = toolkit.systemClipboard
                clipboard.setContents(stringSelection, null)
            } catch (_: Exception) {}
        }
    }

    override fun keyReleased(p0: KeyEvent?) {}
}