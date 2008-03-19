INCDIR=../../../lib/macros
V=$1

asm()
{
	ca65 -v -t apple2 -I ../../disk2 -I $INCDIR -o $1.o65 -D VERSION=$V -D NODELAY ../../disk2/$1.s65
}

asm disk2romc600
ld65 -v -C ../../disk2/disk2.cfg disk2romc600.o65
