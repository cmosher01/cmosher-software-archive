SETLOCAL



SET ROOTDIR=%~dp0
SET ROOTDIR=%ROOTDIR:~0,-1%

IF EXIST "%ROOTDIR%\config.bat" CALL "%ROOTDIR%\config.bat"

IF (%ECHO%)==() SET ECHO=OFF
ECHO %ECHO%

IF DEFINED CC65_HOME GOTO :OKCC65
ECHO Must define CC65_HOME to install directory of CC65
ENDLOCAL
EXIT /B 1
:OKCC65

SET CC65DIR=%CC65_HOME%\bin

IF DEFINED ACDT_HOME GOTO :OKACDT
ECHO Must define ACDT_HOME to install directory of Apple ][ Command-Line Disk-Image Tools
ENDLOCAL
EXIT /B 1
:OKACDT

SET A2CDT=%ACDT_HOME%\a2cdt.jar

IF EXIST build\NUL RMDIR /Q /S build
MKDIR build
CD build



CALL "%ROOTDIR%\dos\build"
IF ERRORLEVEL 1 GOTO :EOF

CALL "%ROOTDIR%\firmware\build"
IF ERRORLEVEL 1 GOTO :EOF



ENDLOCAL
