#!/bin/bash
A2CDT=../../../../a2cdt/a2cdt.jar
INCDIR=../../../lib/macros

asm()
{
	ca65 -v -t apple2 -I .. -I $INCDIR -o $1.o65 -D VERSION=$V -D NODELAY ../$1.s65
}


bld()
{
	V=$1

	rm -Rf dos$V
	mkdir dos$V
	cd dos$V

	asm reloc
	asm filemgr
	asm main
	asm boot1
	asm boot2
	asm rwts
	asm rwtsapi

	ld65 -v -C ../dos.ld65config -m dos.map reloc.o65 main.o65 filemgr.o65 boot1.o65 boot2.o65 rwts.o65 rwtsapi.o65

	if [ $V -lt 330 ]
	then
		java -cp $A2CDT dd --skip=0x1B00 <dos >dos.d13
		java -cp $A2CDT dd --count=0x1B00 <dos >>dos.d13
		java -cp $A2CDT dd --count=0xB800 --const=0 >>dos.d13
		java -cp $A2CDT CreateCatalog --version=$V >>dos.d13
		java -cp $A2CDT dd --count=0xDD00 --const=0 >>dos.d13

		java -cp $A2CDT ConvertD13toNibble <dos.d13 >dos.nib
	else
		java -cp $A2CDT dd --skip=0x1B00 <dos >dos.do
		java -cp $A2CDT dd --count=0x1B00 <dos >>dos.do
		java -cp $A2CDT dd --count=0xEB00 --const=0 >>dos.do
		java -cp $A2CDT CreateCatalog --version=$V >>dos.do
		java -cp $A2CDT dd --count=0x11000 --const=0 >>dos.do

		java -cp $A2CDT ConvertD16toNibble <dos.do >dos.nib
	fi

	cd ..
}

bld 310
bld 320
bld 321
bld 330
bld 331
bld 332
