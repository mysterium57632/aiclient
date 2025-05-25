package de.paull.gui

import de.paull.gui.components.Stats
import de.paull.text.TextField
import de.paull.net.RequestParser

var blocked = false
    private set

fun onEnter(raw: String, field: TextField) {
    val img = Frame.FRAME?.master?.shot?.rawImage
    if (img == null) RequestParser(field).send(raw)
    else RequestParser(field).send(raw, img)
    block()
}

fun onResponse(text: TextField?, resp: String, tok: Int) {
    text?.addAI(resp) ?: Frame.FRAME?.master?.chats?.currentChat?.addAI(resp) ?: return
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