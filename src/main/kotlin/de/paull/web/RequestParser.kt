package de.paull.web

import de.paull.gui.onResponse
import de.paull.text.TextField

class RequestParser(private var field: TextField) {

    private var role: String? = null

    init {
        val role = "Respond concisely and minimally."
    }

    fun send(str: String, img: String = "") {
        Thread {
            val r = Request("")
            val (message, tokens) = r.set(str, img).send() ?: return@Thread
            onResponse(field, message, tokens)
        }.start()
    }
}