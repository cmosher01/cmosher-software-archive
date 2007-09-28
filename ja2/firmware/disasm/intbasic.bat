rem C:\apple2\windows_programs\assemblers\cc65\bin\da65 -v -i intbasic.da65info
C:\apple2\windows_programs\assemblers\cc65\bin\ca65 -v -t apple2 -l -o intbasic_ca65.obj intbasic.a65
C:\apple2\windows_programs\assemblers\cc65\bin\ld65 -v -C intbasic.ld65config -m intbasic.map -o intbasic.exe intbasic_ca65.obj
