CD ..\SRC
%CC65DIR%\ca65 -v -l -t apple2 -D VERSION=1 -o ..\DISASM\%1.obj %1.a65
CD ../DISASM
