#!/bin/sh
# read /var/log/lavapool.log file from standard input
# extract rows indicating adding number to the pool
# extract columns: seconds-since-epoch,numbers-added,new-pool-size
grep poollen | cut -d ' ' -f 1,5,7 | sed 's/\([0123456789]*\)\..* \([0123456789]*\), \(.*\)/\1,\2,\3/'
