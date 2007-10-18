SETLOCAL



SET ROOTDIR=%~dp0
SET ROOTDIR=%ROOTDIR:~0,-1%

SET CC65DIR=C:\cc65\bin
SET ACDTDIR=C:\aaaws44\DosMasterToImage

SET A2CDT=%ACDTDIR%\a2cdt.jar

IF EXIST build\NUL RMDIR /Q /S build
MKDIR build
CD build



CALL %ROOTDIR%\dos\build
IF ERRORLEVEL 1 GOTO :EOF

CALL %ROOTDIR%\firmware\build
IF ERRORLEVEL 1 GOTO :EOF



ENDLOCAL
