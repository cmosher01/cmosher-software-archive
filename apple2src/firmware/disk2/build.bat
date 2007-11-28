:BUILD
SETLOCAL

IF NOT DEFINED ROOTDIR GOTO :EOF

SET SRCDIR=%~dp0
SET SRCDIR=%SRCDIR:~0,-1%
SET INCDIR=%ROOTDIR%\lib\macros





SET VERS=%1

CALL :ASM disk2romc600 %VERS%
"%CC65DIR%\ld65" -v -C "%SRCDIR%\disk2.cfg" -m slot6.map disk2romc600.o65



ENDLOCAL
GOTO :EOF









:ASM
SETLOCAL

COPY /Y "%SRCDIR%\%~1.s65" .

"%CC65DIR%\ca65" -v -l -t apple2 -I "%SRCDIR%" -I "%INCDIR%" -o %~1.o65 -D VERSION=%~2 -D NODELAY %~1.s65
SET /A ERR=%ERRORLEVEL%

DEL /F /Q %~1.s65

IF %ERR% NEQ 0 EXIT /B %ERR%

ENDLOCAL
GOTO :EOF