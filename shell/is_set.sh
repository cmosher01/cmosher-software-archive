#!/bin/sh
set -x
if [ "${FOO}" ] ; then
  echo "is set"
else
  echo "is not set"
fi
