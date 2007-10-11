:BUILD
SETLOCAL

IF NOT DEFINED ROOTDIR GOTO :EOF

SET SRCDIR=%~dp0
SET SRCDIR=%SRCDIR:~0,-1%
SET INCDIR=%ROOTDIR%\lib\macros



SET /A VERS=%1



IF %VERS% GTR 0 GOTO :APPLE2

:APPLE1

CALL :ASM apple1mon 0

%CC65DIR%\ld65 -v -C %SRCDIR%\apple1mon.ld65config -m monitor.map apple1mon.o65
IF ERRORLEVEL 1 GOTO :EOF

GOTO :DONE




:APPLE2
CALL :ASM lores %VERS%
CALL :ASM disasm %VERS%
CALL :ASM debug %VERS%
CALL :ASM paddles %VERS%
CALL :ASM display1 %VERS%
CALL :ASM math %VERS%
CALL :ASM display2 %VERS%
CALL :ASM cassette %VERS%
CALL :ASM keyin %VERS%
CALL :ASM monitor %VERS%
CALL :ASM vectors %VERS%
%CC65DIR%\ld65 -v -C monitor.ld65config -m monitor.map lores.o65 disasm.o65 debug.o65 paddles.o65 display1.o65 math.o65 display2.o65 cassette.o65 keyin.o65 monitor.o65 vectors.o65
GOTO :DONE



:DONE
ENDLOCAL
GOTO :EOF









:ASM
SETLOCAL

COPY /Y %SRCDIR%\%~1.s65 .

%CC65DIR%\ca65 -v -l -t apple2 -I %SRCDIR% -I %INCDIR% -o %~1.o65 -D VERSION=%~2 %~1.s65
SET /A ERR=%ERRORLEVEL%

DEL /F /Q %~1.s65

IF %ERR% NEQ 0 EXIT /B %ERR%

ENDLOCAL
GOTO :EOF
