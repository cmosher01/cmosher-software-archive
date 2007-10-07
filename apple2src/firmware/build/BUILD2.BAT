SET CC65DIR=C:\apple2\windows_programs\assemblers\cc65\bin

CALL BUILD\ASM MON

%CC65DIR%\ld65 -v -C build\MON.ld65config work\MON.OBJ
