INCDIR=../../../lib/macros

asm()
{
	ca65 -v -t apple2 -I ../../other -I $INCDIR -o $1.o65 ../../other/$1.s65
}

asm fp1
asm miniasm1
asm fp2
asm miniasm2
asm f669
asm sweet16
ld65 -v -C ../../other/other.cfg fp1.o65 miniasm1.o65 fp2.o65 miniasm2.o65 f669.o65 sweet16.o65
