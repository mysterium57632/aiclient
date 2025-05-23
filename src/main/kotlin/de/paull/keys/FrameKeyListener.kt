package de.paull.keys

import de.paull.gui.Master
import de.paull.gui.blocked
import de.paull.gui.components.Chats
import de.paull.gui.components.TerminalEmulator
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class FrameKeyListener(master: Master) : KeyListener {

    private val promt: TerminalEmulator = master.prompt
    private var inChat: Boolean = false

    override fun keyTyped(e: KeyEvent?) {
        val c = e?.keyChar ?: return
        if (e.keyChar == '\t') return
        if (inChat) onTab()
        if (c.code == KeyEvent.VK_BACK_SPACE) {
            if (promt.typed.isNotEmpty())
                promt.typed = promt.typed.dropLast(1)
        } else if (!e.isControlDown && c.isLetterOrDigit() || c.isWhitespace() || c in TerminalEmulator.Companion.SONDERZEICHEN) {
            promt.typed += c
        }
    }

    override fun keyPressed(e: KeyEvent?) {
        if (e?.keyCode == KeyEvent.VK_ENTER) return onEnter()
        if (e?.keyCode == KeyEvent.VK_TAB) return onTab()
        if (e?.keyCode == KeyEvent.VK_DOWN) return onDown()
        if (e?.keyCode == KeyEvent.VK_UP) return onUp()
        if (e?.keyCode == KeyEvent.VK_V && e.isControlDown) return onCtrlV()
        if (e?.keyCode == KeyEvent.VK_C && e.isControlDown) return onCtrlC()
    }

    // On Enter: Send Message or clear terminal / create new Chat
    private fun onEnter() {
        if (inChat) {
            if (promt.chats.highlightIndex == 0)
                promt.clearTerminal()
            return onTab()
        }
        val t = promt.typed.trim()
        if (t.isEmpty()) return
        if (t == "clear")
            return promt.clearTerminal()
        if (blocked) return
        val current = promt.sendMessage(t)
        return de.paull.gui.onEnter(t, current)
    }

    private fun onTab() {
        inChat = inChat.not()
        if (inChat) {
            promt.stopCursor()
            promt.chats.highlightIndex = 0
            return
        }
        promt.start()
        if (promt.chats.highlightIndex == 0)
            promt.chats.highlightIndex = 1
    }

    private fun onDown() {
        if (!inChat) return
        if (promt.chats.highlightIndex + 1 > promt.chats.listSize) return
        promt.chats.highlightIndex++
    }

    private fun onUp() {
        if (!inChat) return
        if (promt.chats.highlightIndex <= 0) return
        promt.chats.highlightIndex--
    }

    private fun onCtrlV() {
        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            val paste = clipboard.getData(DataFlavor.stringFlavor) as String
            promt.typed += paste.trim()
            return
        } catch (_: Exception) {}
    }

    private fun onCtrlC() {
        try {
            val text = promt.chats.currentChat?.lastAiMessage ?: return
            val stringSelection = StringSelection(text)
            val toolkit = Toolkit.getDefaultToolkit()
            val clipboard = toolkit.systemClipboard
            clipboard.setContents(stringSelection, null)
        } catch (_: Exception) {}
    }

    override fun keyReleased(p0: KeyEvent?) {}
}