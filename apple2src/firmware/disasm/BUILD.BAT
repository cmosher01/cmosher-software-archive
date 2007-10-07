SET CC65DIR=C:\apple2\windows_programs\assemblers\cc65\bin

CALL ASM A1MON

%CC65DIR%\ld65 -v -C A1MON.ld65config A1MON.OBJ
