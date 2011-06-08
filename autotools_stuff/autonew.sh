#!/bin/sh

#
# Abort on error
#
set -e





#
# Parse command line options
#
DELEXIST=
HELP=
while getopts dh opt
do
	case $opt in
		d) DELEXIST=1 ;;
		h|?) HELP=1 ;;
	esac
done
shift `expr $OPTIND - 1`


#
# Display help message
#
if [ "$HELP" ]
then
	printf "Usage: %s: [-d] PACKAGE\n" $0
	exit 1
fi

#
# Check parameters
#
if [ -z "$1" ]
then
	echo "Missing PACKAGE name" >&2
	exit 1
fi



#
# sanity check to make sure we have autoconf
#
autoconf --version



#
# Check for existence of package directory, and delete
# it if the user explicitly tells us to.
#
if [ -d $1 ]
then
	if [ "$DELEXIST" ]
	then
		rm -Rf $1
	else
		echo "Error: $1 exists; specify -d to delete it first" >&2
		exit 1
	fi
fi

#
# Create the directory for the package and go into it
#
mkdir $1
cd $1

#
# Create the standard directory for source files, src
#
mkdir src

#
# Use the m4 directory for all our local m4 macros
#
mkdir m4

#
# Create skeleton configure.ac file with
# autoscan, then fix it up with sed script
#
autoscan
cat <<EOF >configure.sed
s/FULL-PACKAGE-NAME/$1/
s/VERSION/0.1/
s/,* *\[BUG-REPORT-ADDRESS\]//
/AC_INIT/ {a\
AC_CONFIG_AUX_DIR([build-aux])
a\
AM_INIT_AUTOMAKE([-Wall -Werror])
}
/AC_OUTPUT/ i\
AC_CONFIG_FILES([Makefile src/Makefile])
EOF
sed -f configure.sed <configure.scan >configure.ac



#
# Create skeleton GNU doc files
cat <<EOF >NEWS
$1 NEWS - User visible changes.

* Noteworthy changes in version 0.1

** Package created: `date --rfc-3339=date`
EOF

touch README AUTHORS ChangeLog



#
# Create bare Makefile.am files
#
cat <<EOF >Makefile.am
#@configure_input@
ACLOCAL_AMFLAGS=-I m4 --install
SUBDIRS=src
EOF

cat <<EOF >src/Makefile.am
#@configure_input@
EOF



#
# Create bootstrap script and run it
#
cat <<EOF >bootstrap
autoreconf --install
EOF
chmod a+x bootstrap
./bootstrap



#
# Delete all files that should not be placed
# under version control.
#
rm INSTALL
rm configure.sed
rm autoscan*.log
rm configure.scan
rm Makefile.in
rm aclocal.m4
rm -Rf autom4te.cache
rm -Rf build-aux
rm configure
rm src/Makefile.in



exit 0
