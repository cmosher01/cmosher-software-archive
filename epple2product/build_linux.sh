#!/bin/bash

usage()
{
	echo "usage: $0 [OPTIONS]"
	echo "options:"
	echo "  -c   do a clean build"
	echo "  -d   deploy after building"
	echo "  -w   only build web site"
}

set -e

PREFIX=/usr
SYSCONFDIR=/etc

unset CLEAN
unset DEPLOY
unset WEBONLY
while getopts "cdw" OPT
do
	case "$OPT" in
		\?) usage ; exit ;;
		c) CLEAN=1 ;;
		d) DEPLOY=1 ;;
		w) WEBONLY=1 ;;
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

if [[ ! "$WEBONLY" ]]
then
echo --------------------------------------------------------------------------------epple2
date --rfc-3339=seconds
mkdir -p epple2
cd epple2
if [[ -x ./config.status ]]
then
	./config.status
else
	$SRC/epple2/configure --prefix=$PREFIX --sysconfdir=$SYSCONFDIR
fi
make package
cd ..

echo --------------------------------------------------------------------------------epple2sys
date --rfc-3339=seconds
mkdir -p epple2sys
cd epple2sys
$SRC/epple2sys/configure -p $PREFIX
make package
cd ..

echo --------------------------------------------------------------------------------epple2cards
date --rfc-3339=seconds
mkdir -p epple2cards
cd epple2cards
$SRC/epple2cards/configure -p $PREFIX
make package
cd ..

echo --------------------------------------------------------------------------------apple2sys
date --rfc-3339=seconds
mkdir -p apple2sys
cd apple2sys
$SRC/apple2src/system/configure -p $PREFIX
make package
cd ..

echo --------------------------------------------------------------------------------apple2dos
date --rfc-3339=seconds
mkdir -p apple2dos
cd apple2dos
$SRC/apple2src/dos3x/configure -p $PREFIX
make package
cd ..
fi

echo --------------------------------------------------------------------------------epple2web
date --rfc-3339=seconds
mkdir -p epple2web
cp -vf $SRC/epple2web/* epple2web

if [[ ! "$WEBONLY" ]]
then
find . \( -path ./epple2web -a -prune \) -o \( \( -path \*/RPMS/\*.rpm -o -name \*.deb -o -path \*/SOURCES/\*.tar.gz \) -a -print \) | xargs -I {} cp -vf {} epple2web
fi

cd epple2web

ln -f -s -T `pwd` $SRC/local

if [[ "$DEPLOY" ]]
then
	echo --------------------------------------------------------------------------------deploy
	date --rfc-3339=seconds
	sudo -u www-data cp -vf * $SRC/deploy
fi

date --rfc-3339=seconds
