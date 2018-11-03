#!/bin/sh
if [ "$1" ] ; then
  echo "given: $1"
else
  echo "[absent]"
fi

if [ ! "$1" ] ; then
  echo "[absent]"
fi
