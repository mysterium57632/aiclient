package de.paull

import de.paull.lib.files.ConfigHandler
import de.paull.lib.output.*
import de.paull.net.NotifyServer

fun main() {
    ConfigHandler("config.cfg", "CONFIG", Main())
    Output(ConfigHandler.get("DEBUG").equals("true"), "de.paull")

    NotifyServer()
}

class Main : ConfigHandler.InitializeConfig {

    override fun iniDefaultConfig(): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["DEBUG"] = "false"
        map["LOG_FILE"] = "log/webserver.log"
        map["ERR_FILE"] = "log/error.log"
        map["API_KEY"] = "-"
        map["NOTIFY_PORT"] = "42000"
        map["CREATE_SCRIPT"] = "true"
        return map
    }
}

// TODO
// add history in console (last questions)
// remove screenshoot again
// fancy animation
// Text when coping to clipbord
// Fix screenshot white edge