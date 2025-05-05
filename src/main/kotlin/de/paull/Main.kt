package de.paull

import de.paull.lib.files.ConfigHandler
import de.paull.lib.output.*
import org.jnativehook.GlobalScreen

fun main() {
    ConfigHandler("config.cfg", "CONFIG", Main())
    Output(ConfigHandler.get("DEBUG").equals("true"), "de.paull")

    val keylis = GlobalKeyListener()

    GlobalScreen.registerNativeHook()
    GlobalScreen.addNativeKeyListener(keylis)
}

class Main : ConfigHandler.InitializeConfig {

    override fun iniDefaultConfig(): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["DEBUG"] = "false"
        map["LOG_FILE"] = "log/webserver.log"
        map["ERR_FILE"] = "log/error.log"
        map["API_KEY"] = "-"
        return map
    }

}

// TODO
// add fps
// add title
// add history in console (last questions)
// remove screenshoot again
// fancy animation
// see how many tokens => And money spend
// Text when coping to clipbord
// Fix screenshot white edge