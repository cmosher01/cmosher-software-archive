#!/bin/bash
if [ -d .svn ]
then
	:
else
	echo ".svn directory not found"
	exit 1
fi
if [ -x co_linux.sh ]
then
	:
else
	echo "must cd to directory containing co_linux.sh"
fi
SVNREPO=`cat .svn/entries | head -n 6 | tail -n 1`
svn co $SVNREPO/epple2sdl epple2
svn co $SVNREPO/epple2cards
svn co $SVNREPO/epple2sys
svn co $SVNREPO/epple2web
svn co $SVNREPO/apple2src

sudo apt-get install autoconf libsdl1.2-dev rpm fakeroot alien openjdk-6-jre-headless

cd epple2
chmod a+x bootstrap
./bootstrap
cd -

# download from cc65,org: RedHat: cc65-VERS.i386.rpm, cc65-apple2-VERS.i386.rpm
# sudo alien -i cc65-VERS.i386.rpm
# sudo alien -i cc65-apple2-VERS.i386.rpm
