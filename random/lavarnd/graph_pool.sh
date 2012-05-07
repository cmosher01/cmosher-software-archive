#!/bin/sh
if [ -r "$1" ]
then

BASE=`head -n 1 $1 | cut -d ',' -f 1`

gnuplot <<EOF
set terminal dumb size 240 80
set datafile separator ","
plot '$1' using (\$1-$BASE):(\$3/1000000) with lines, '$1' using (\$1-$BASE):(\$2) with lines
EOF

else
echo "usage: $0 lava_poolsize_file"
fi
