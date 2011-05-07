SET CC65_HOME=C:\apple2\windows_programs\assemblers\cc65



SET CC65DIR=%CC65_HOME%\bin

"%CC65DIR%\ca65" -v -t apple2 -o clock.o65 clock.s65
"%CC65DIR%\ld65" -v -C clock.cfg clock.o65
DEL clock.o65

"%CC65DIR%\ca65" -v -t apple2 -o stdin.o65 stdin.s65
"%CC65DIR%\ld65" -v -C stdin.cfg stdin.o65
DEL stdin.o65

"%CC65DIR%\ca65" -v -t apple2 -o stdout.o65 stdout.s65
"%CC65DIR%\ld65" -v -C stdout.cfg stdout.o65
DEL stdout.o65
