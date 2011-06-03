#!/bin/sh







DELEXIST=
HELP=
while getopts dh opt
do
	case $opt in
		d)		DELEXIST=1
					;;
		h|?)	HELP=1
					;;
	esac
done
shift `expr $OPTIND - 1`


#
#
#
if [ "$HELP" ]
then
	printf "Usage: %s: [-d] PACKAGE\n" $0
	exit 1
fi

if [ -z "$1" ]
then
	echo "Missing PACKAGE name" >&2
	exit 1
fi

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

mkdir $1
cd $1
mkdir src
mkdir m4
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





cat <<EOF >Makefile.am
ACLOCAL_AMFLAGS=-I m4 --install
SUBDIRS=src
EOF

touch src/Makefile.am

cat <<EOF >NEWS
$1 NEWS - User visible changes.

* Noteworthy changes in version 0.1

** Package created: `date --rfc-3339=date`
EOF

touch README AUTHORS ChangeLog

cat <<EOF >bootstrap
autoreconf --install
EOF
chmod a+x bootstrap
./bootstrap



# delete all files that should not be under version control
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
