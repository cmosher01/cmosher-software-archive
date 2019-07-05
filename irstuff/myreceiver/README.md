# Infrared Signal Reader

Program (Visual C++) that reads IR remote signals, and
creates a log file containing signal timings and analysis.
It is mostly a tool to help you study the remote control
signals.

You need to build a device that readsthe infrared signals
from the remote and toggles the Carrier Detect signal on the
serial port.

Schematic

               +-------------------+--------+
               |                   |        |
               |                 ----- C1   |
               |                 -----      |   +----------+
        +pin-5-+   D1             +|        +---+ GND      |
     DB9|pin-7-----|>|---+--/\/\---+------------+ Vcc   I1 |
        +pin-1-+         |   R1             +---+ Vout     |
               |         \                  |   +----------+
               |         /                  |
               |         \  R2              |
               |         /                  |
               |         |                  |
               +---------+------------------+

Parts

 I1 | IR detector module
 D1 | 1N914 diode
 C1 | 10 uF cap
 R1 | 4.7K res
 R2 | 10K res
DB9 | D-sub 9-pin female
