#!/bin/bash

# sanity checks
if [[ "$OSTYPE" != msys ]]
then
	echo "Error: must run under MSYS." >&2
	exit 1
fi

if [[ ! -d epple2 ]]
then
	echo "Cannot find epple2 directory in current directory" >&2
	exit 1
fi




usage()
{
	echo "usage: $0 [OPTIONS]"
	echo "options:"
	echo "  -c   do a clean build"
}

set -e

PREFIX=/usr
SYSCONFDIR=/etc

unset CLEAN
while getopts "cd" OPT
do
	case "$OPT" in
		\?) usage ; exit 0 ;;
		c) CLEAN=1 ;;
	esac
done

SRC=`pwd`

if [[ ! "$CLEAN" ]]
then
        LAST_BUILD_DIR=`ls -1rd /tmp/epple2-* 2>/dev/null | head -n 1`
fi
if [[ ! "$LAST_BUILD_DIR" ]]
then
	VERS=`date +%s`
	LAST_BUILD_DIR=/tmp/epple2-${VERS}
	mkdir ${LAST_BUILD_DIR}
fi
cd ${LAST_BUILD_DIR}

echo --------------------------------------------------------------------------------epple2
date --rfc-3339=seconds
mkdir -p epple2
cd epple2
if [[ -x ./config.status ]]
then
	./config.status
else
	$SRC/epple2/configure --prefix= CXXFLAGS="-I/usr/include -O4 -msse3" CFLAGS="-I/usr/include" LDFLAGS="-L/usr/lib -mconsole -mthreads -mno-cygwin"
fi
make
cd ..

echo --------------------------------------------------------------------------------epple2sys
date --rfc-3339=seconds
mkdir -p epple2sys
cd epple2sys
$SRC/epple2sys/configure -p $PREFIX
make
cd ..

echo --------------------------------------------------------------------------------epple2cards
date --rfc-3339=seconds
mkdir -p epple2cards
cd epple2cards
$SRC/epple2cards/configure -p $PREFIX
make
cd ..

echo --------------------------------------------------------------------------------apple2sys
date --rfc-3339=seconds
mkdir -p apple2sys
cd apple2sys
$SRC/apple2src/system/configure -p $PREFIX
make
cd ..

echo --------------------------------------------------------------------------------apple2dos
date --rfc-3339=seconds
mkdir -p apple2dos
cd apple2dos
$SRC/apple2src/dos3x/configure -p $PREFIX
make
cd ..

date --rfc-3339=seconds
