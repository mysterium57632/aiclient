package de.paull.web

import de.paull.gui.onResponse
import de.paull.text.TextField

class RequestParser(private var field: TextField) {

    private var role: String = "Respond concisely and minimally."

    init {
    }

    fun send(str: String, img: String = "") {
        Thread {
            val r = Request(role)
            val (message, tokens) = r.set(str, img).send() ?: return@Thread
            onResponse(field, message, tokens)
        }.start()
    }
}