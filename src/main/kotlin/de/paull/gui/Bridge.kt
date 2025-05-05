package de.paull.gui

import de.paull.gui.components.Screenshot
import de.paull.gui.components.TerminalEmulator
import de.paull.web.Request

class Bridge(private val master: Master) {

    private val text: TerminalEmulator = master.prompt
    private val shot: Screenshot = master.shot

    var blocked = false
        private set

    fun onEnter(raw: String) {
        val img = shot.rawImage
        if (img == null) Request(:: onResponse, :: onError).send(raw)
        else Request(:: onResponse, :: onError).send(raw, img)
        block()
    }

    fun onResponse(resp: String, tok: Int) {
        text.text.addAI(resp)
        master.stats.update(tok)
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