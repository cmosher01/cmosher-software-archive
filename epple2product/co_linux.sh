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

sudo apt-get install autoconf
sudo apt-get install libsdl1.2-dev
sudo apt-get install rpm
sudo apt-get install fakeroot
sudo apt-get install alien

# download cc65, cc65-apple2
# use alien to convert to deb files
# dpkg -i them

cd epple2
chmod a+x bootstrap
./bootstrap
cd -
