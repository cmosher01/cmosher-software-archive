SET CC65DIR=C:\cc65\bin

CALL ASM intbasic

%CC65DIR%\ld65 -v -C intbasic.ld65config intbasic.OBJ
