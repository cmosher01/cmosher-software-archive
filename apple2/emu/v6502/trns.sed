#!/bin/sed -f
/^[^[]/ d
s/\[//
s/^[^0-9]*'t\([0-9]*\)', \([0-9]*\), \([0-9]*\), \([0-9]*\).*/\1 \2 \3 \4/
