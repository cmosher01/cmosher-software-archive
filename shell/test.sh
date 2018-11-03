#!/bin/sh
set -x

# test.sh "! x"

#wrong
if [ $1 = foo ] ; then
  echo bar
else
  echo no
fi

#right
if [ "$1" = foo ] ; then
  echo bar
else
  echo no
fi
