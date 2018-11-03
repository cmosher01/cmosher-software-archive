#!/bin/sh

p=$1
echo "x: will write to $p"

printf "%s\n" "abc" >$p

echo "x: done"
