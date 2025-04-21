package de.paull.gui

import de.paull.gui.components.Screenshot
import de.paull.gui.components.TerminalEmulator
import de.paull.web.Request

class Bridge(private val text: TerminalEmulator, private val shot: Screenshot) {

    var blocked = false
        private set

    fun onEnter(raw: String) {
        val img = shot.rawImage
        if (img == null) Request(:: onResponse, :: onError).send(raw)
        else Request(:: onResponse, :: onError).send(raw, img)
        block()
    }

    fun onResponse(resp: String, tok: Int) {
        text.addAI(resp)
        unblock()
        println(tok)
    }

    fun onError(mess: String) {
        unblock()
        println(mess)
    }

    private fun block() {
        blocked = true
        text.startThinkTimer()
        shot.clearImage()
    }

    private fun unblock() {
        blocked = false
        text.stopThinkTimer()
    }
}