package de.paull.net

import de.paull.gui.Frame
import de.paull.lib.files.ConfigHandler
import de.paull.lib.util.Table
import java.io.File
import java.io.IOException
import java.net.ServerSocket

/**
 * This is needed to inform this program about a shortcut event.
 */
class NotifyServer {

    private var server: ServerSocket? = null
    private var port: Int = ConfigHandler.getInteger("NOTIFY_PORT") ?: 42000
    private var running = true

    init {
        try {
            server = ServerSocket(port)

            Thread {
                acceptLoop()
            }.start()

            val arr = arrayOf(
                arrayOf("Port", "$port"),
            )
            println(Table.convert(arr, "Notify-Server online"))
        } catch (e: IOException) {
            println("Unable to start Notify-Server on port $port.\n${e.message}")
        }

        createScript()
    }

    /**
     * Stops the server and closes all connections.
     */
    fun stop() {
        running = false
        server?.close()
        server = null
        println("Notify-Server stopped")
    }

    fun acceptLoop() {
        while (running) {
            try {
                val client = server?.accept() ?: continue
                client.close()
                onEvent()
            } catch (_: IOException) {}
        }
    }

    /**
     * This method is called when a connection is established.
     */
    fun onEvent() {
        if (Frame.FRAME != null) return
        Frame.FRAME = Frame()
    }

    fun createScript() {
        if (ConfigHandler.get("CREATE_SCRIPT")?.equals("true")?.not() ?: false) return
        var grammar = "Created"
        val str = """
            #!/bin/bash

            # This script is used to notify the program when a GNOME (or other desktop) shortcut is triggered.
            # Because under Wayland capturing global key events inside Java is prohibited,
            # we instead need to define a GNOME Custom Shortcut that runs this script.

            # This line connects to the local NotifyServer running on the port specified in the config file.
            # It uses 'nc' (netcat) to open a TCP connection to localhost:$port and immediately closes it.
            # The Java NotifyServer listens for incoming connections; when a client connects,
            # the application will create a new window.

            nc localhost $port < /dev/null
        """.trimIndent()

        val f = File("notify.sh")

        try {
            if (f.exists() && f.readText().trim() == str) return
            else if (f.exists()) {
                f.delete()
                grammar = "Updated"
            }
        } catch (_: Exception) {}

        try {
            if (!f.exists()) f.createNewFile()
            f.writeText(str)
            f.setExecutable(true)
            println("$grammar notify.sh script in\n${f.absolutePath}")
        } catch (e: Exception) {
            println("Unable to create notify.sh script.\n${e.message}")
        }
    }

}