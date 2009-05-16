#!/bin/sh
DIR=$1
shift 1
case "$DIR" in
	"" | ".")
		D=
	;;
	*)
		D=$DIR/
	;;
esac
gcc -MM "$@" | sed -e "s@^\(.*\)\.o:@$D/\1.d $D/\1.o:@"
