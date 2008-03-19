INCDIR=../../../lib/macros
V=$1

asm()
{
	ca65 -v -t apple2 -I ../../monitor -I $INCDIR -o $1.o65 -D VERSION=$V ../../monitor/$1.s65
}

if [ $V -eq 0 ]
then
	asm apple1mon 0
	ld65 -v -C ../../monitor/apple1mon.cfg apple1mon.o65
else
	asm lores
	asm disasm
	asm debug
	asm paddles
	asm display1
	asm math
	asm display2
	asm cassette
	asm keyin
	asm monitor
	asm vectors
	ld65 -v -C ../../monitor/monitor.cfg lores.o65 disasm.o65 debug.o65 paddles.o65 display1.o65 math.o65 display2.o65 cassette.o65 keyin.o65 monitor.o65 vectors.o65
fi
