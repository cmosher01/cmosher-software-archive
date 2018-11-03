#!/bin/sh
set -e
myfile=/filenotfound
result=`cat /etc/fstab`
printf -- ">$result<\n"
result=`if [ -r $myfile ] ; then cat $myfile ; fi`
printf -- ">$result<\n"
