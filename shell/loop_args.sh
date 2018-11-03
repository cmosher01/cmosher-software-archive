#!/bin/sh

x=$1
while [ "$x" ] ; do
    printf "argument: %s\n" "$x"
    shift
    x=$1
done
