:BUILD
SETLOCAL

IF NOT DEFINED ROOTDIR GOTO :EOF

SET SRCDIR=%~dp0
SET SRCDIR=%SRCDIR:~0,-1%
SET INCDIR=%ROOTDIR%\lib\macros





SET VERS=%1

CALL :ASM applesoft %VERS%
"%CC65DIR%\ld65" -v -C "%SRCDIR%\as.cfg" -m applesoft.map applesoft.o65



ENDLOCAL
GOTO :EOF









:ASM
SETLOCAL

COPY /Y "%SRCDIR%\%~1.s65" .

"%CC65DIR%\ca65" -v -l -t apple2 -I "%SRCDIR%" -I "%INCDIR%" -o %~1.o65 %~1.s65
SET /A ERR=%ERRORLEVEL%

DEL /F /Q %~1.s65

IF %ERR% NEQ 0 EXIT /B %ERR%

ENDLOCAL
GOTO :EOF
