# bt-relays
This repository contains a simple Android app which talks to a home built BlueTooth device which in turn controls two 5v relays.
This device is running a custom firmware. The idea is quite similar to [this one](https://github.com/pfalcon/blutunode), but has a
somewhat different protocol and goal. On startup this application performs initial connection with the device.
Relays are turned on/off whenver the user clicks on volume up/down buttons on the phone.
This device and the program were specifically designed as a remote control for the standing desk in the office.

AT commands used by this app:
- AT+DIR=XXXX -- set direction of GPIO ports
- AT+PIN=XXXX -- set output value of GPIO ports

XXXX here is a decimal number representing the bits state.
