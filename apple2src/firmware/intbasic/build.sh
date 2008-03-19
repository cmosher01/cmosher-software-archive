INCDIR=../../../lib/macros
V=$1

asm()
{
	ca65 -v -t apple2 -I ../../intbasic -I $INCDIR -o $1.o65 -D VERSION=$V -D BUGFIX ../../intbasic/$1.s65
}

asm intbasic
ld65 -v -C ../../intbasic/intbasic.cfg intbasic.o65
