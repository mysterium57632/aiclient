package de.paull.web

import de.paull.gui.onError
import de.paull.gui.onResponse
import de.paull.lib.files.ConfigHandler
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val url: String = "https://api.openai.com/v1/chat/completions"
val api: String = ConfigHandler.get("API_KEY")

private fun randomErrorMessage(): String {
    val set = setOf("Fuck", "An error occurred", "There was an error",
        "Sorry, i am unable to recover the response", "Shit", "Fuck you")
    return set.random()
}

class Request(private val role: String) {

    private var text = ""
    private var img = ""

    fun set(str: String, img: String = ""): Request {
        this.text = str
        this.img = img
        return this
    }

    fun send(): Pair<String, Int>? {
        val client: HttpClient = HttpClient.newHttpClient()
        val max = 300
        val body = createBody(role, text, max)

        val request = HttpRequest.newBuilder()
            .uri(URI(url))
            .setHeader("Authorization", "Bearer $api")
            .setHeader("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        val resp = response.body() ?: return null

        val parser = JSONParser()
        val json = parser.parse(resp) as? JSONObject ?: return null

        if (errorHandling(json)) return null
        val tokens = getTokens(json)
        val message = getMessage(json)
        return Pair(message, tokens)
    }

    private fun errorHandling(json:JSONObject): Boolean {
        val error = json["error"] as? JSONObject ?: return false
        val type = error["type"] ?: ""
        if (type == "insufficient_quota")
            onResponse(null, "You exceeded your current quota, please check your plan and billing details.", 0)
        else onError("$type: ${error["message"] ?: "unknown"}")
        return true
    }

    private fun getTokens(json: JSONObject): Int {
        val usage = json["usage"] as? JSONObject ?: return 0
        val tok = usage["total_tokens"] as Long
        return tok.toInt()
    }

    private fun getMessage(json: JSONObject): String {
        val con = (json["choices"] as? JSONArray)
            ?.getOrNull(0) as? JSONObject
        val message = (con?.get("message") as? JSONObject)?.get("content") as? String
        return message ?: randomErrorMessage()
    }

    private fun createBody(role: String, content: String, max: Int): String {
        val mSys = JSONObject()
        mSys["role"] = "system"
        mSys["content"] = role

        val mUsr = JSONObject()
        mUsr["role"] = "user"
        mUsr["content"] = if (img.isEmpty()) content else getWithImage(content)

        val jarr = JSONArray()
        jarr.add(mSys)
        jarr.add(mUsr)

        val json = JSONObject()
        json["model"] = "gpt-4.1-mini"
        json["max_tokens"] = max
        json["temperature"] = 0.2
        json["n"] = 1
        json["messages"] = jarr

        return json.toString()
    }

    private fun getWithImage(content: String): JSONArray {
        val url = JSONObject()
        url["url"] = "data:image/png;base64,$img"

        val txt = JSONObject()
        txt["type"] = "text"
        txt["text"] = content

        val iurl = JSONObject()
        iurl["url"] = "data:image/png;base64,${this.img}"
        iurl["detail"] = "low"

        val img = JSONObject()
        img["type"] = "image_url"
        img["image_url"] = iurl

        val jarr = JSONArray()
        jarr.add(txt)
        jarr.add(img)

        return jarr
    }

}