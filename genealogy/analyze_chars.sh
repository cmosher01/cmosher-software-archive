#!/bin/sh
ls -l $1
head -n 2 $1
od -tx1 -N 32 $1
head -n 100 $1 | grep -E -a '^.?1.? .?C.?H.?A.?R'
echo '-----------------'
