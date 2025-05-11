package de.paull.gui

import de.paull.gui.components.Chats
import de.paull.gui.components.Stats
import de.paull.text.TextField
import de.paull.web.RequestParser

var blocked = false
    private set

fun onEnter(raw: String) {
    val img = Frame.FRAME?.master?.shot?.rawImage
    if (img == null) RequestParser().send(raw)
    else RequestParser().send(raw, img)
    block()
}

fun onResponse(text: TextField?, resp: String, tok: Int) {
    text?.addAI(resp) ?: Chats.currentChat?.addAI(resp) ?: return
    Stats.tokens = tok
    unblock()
}

fun onError(mess: String) {
    unblock()
    println(mess)
}

private fun block() {
    blocked = true
    Frame.FRAME?.master?.prompt?.startThinkTimer() ?: return
    Frame.FRAME?.master?.shot?.clearImage() ?: return
}

private fun unblock() {
    blocked = false
    Frame.FRAME?.master?.prompt?.stopThinkTimer()
}