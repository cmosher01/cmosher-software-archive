SETLOCAL



SET ROOTDIR=%~dp0
SET ROOTDIR=%ROOTDIR:~0,-1%

SET CC65DIR=C:\apple2\windows_programs\assemblers\cc65\bin

IF EXIST build\NUL RMDIR /Q /S build
MKDIR build
CD build



CALL %ROOTDIR%\dos\build
IF ERRORLEVEL 1 GOTO :EOF

CALL %ROOTDIR%\firmware\build
IF ERRORLEVEL 1 GOTO :EOF



ENDLOCAL
