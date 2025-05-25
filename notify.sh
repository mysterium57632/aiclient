#!/bin/bash

# This script is used to notify the program when a GNOME (or other desktop) shortcut is triggered.
# Because under Wayland capturing global key events inside Java is prohibited,
# we instead need to define a GNOME Custom Shortcut that runs this script.

# This line connects to the local NotifyServer running on the port specified in the config file.
# It uses 'nc' (netcat) to open a TCP connection to localhost:42000 and immediately closes it.
# The Java NotifyServer listens for incoming connections; when a client connects,
# the application will create a new window.

nc localhost 42000 < /dev/null