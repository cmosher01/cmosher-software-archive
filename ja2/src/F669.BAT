rem C:\cc65\bin\da65 -v -i F669.da65info
rem C:\cc65\bin\ca65 -v -t apple2 -l -o F669_ca65.obj F669.DA65DISASM
C:\cc65\bin\ca65 -v -t apple2 -l -o F669_ca65.obj F669.A65
C:\cc65\bin\ld65 -v -C F669.ld65config -o F669.exe F669_ca65.obj
