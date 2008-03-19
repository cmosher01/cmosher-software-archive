INCDIR=../../../lib/macros

asm()
{
	ca65 -v -t apple2 -I ../../applesoft -I $INCDIR -o $1.o65 ../../applesoft/$1.s65
}
asm applesoft
ld65 -v -C ../../applesoft/as.cfg applesoft.o65
