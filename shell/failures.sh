#!/bin/sh
(( rs = 0 ))
echo $rs
for x in $@ ; do
  if [ "$x" = "t" ] ; then
    ls bad_file
  else
    ls failures.sh
  fi
  (( rs = rs || $? ))
done
echo $rs
exit $rs
