:BUILD
SETLOCAL

IF NOT DEFINED ROOTDIR GOTO :EOF

SET SRCDIR=%~dp0
SET SRCDIR=%SRCDIR:~0,-1%
SET INCDIR=%ROOTDIR%\lib\macros





CALL :ASM fp1
CALL :ASM miniasm1
CALL :ASM fp2
CALL :ASM miniasm2
CALL :ASM f669
CALL :ASM sweet16
"%CC65DIR%\ld65" -v -C "%SRCDIR%\other.cfg" -m other.map fp1.o65 miniasm1.o65 fp2.o65 miniasm2.o65 f669.o65 sweet16.o65



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