:BUILD
SETLOCAL

IF NOT DEFINED ROOTDIR GOTO :EOF

MKDIR firmware
CD firmware





MKDIR apple1
CD apple1

CALL "%ROOTDIR%\firmware\intbasic\build" 1
IF ERRORLEVEL 1 GOTO :EOF

RENAME intbasic     apple1_A$E000_L$1000_intbasic
RENAME intbasic.map apple1_A$E000_L$1000_intbasic.map



CALL "%ROOTDIR%\firmware\monitor\build" 0
IF ERRORLEVEL 1 GOTO :EOF

RENAME monitor     apple1_A$FF00_L$0100_monitor
RENAME monitor.map apple1_A$FF00_L$0100_monitor.map

CD ..





MKDIR apple2
CD apple2

CALL "%ROOTDIR%\firmware\intbasic\build" 2
IF ERRORLEVEL 1 GOTO :EOF

RENAME intbasic     apple2_A$E000_L$1425_intbasic
RENAME intbasic.map apple2_A$E000_L$1425_intbasic.map



CALL "%ROOTDIR%\firmware\other\build"
IF ERRORLEVEL 1 GOTO :EOF

RENAME other     apple2_A$F425_L$03DB_other
RENAME other.map apple2_A$F425_L$03DB_other.map



CALL "%ROOTDIR%\firmware\monitor\build" 1
IF ERRORLEVEL 1 GOTO :EOF

RENAME monitor     apple2_A$F800_L$0800_monitor
RENAME monitor.map apple2_A$F800_L$0800_monitor.map

CD ..





MKDIR apple2plus
CD apple2plus

CALL "%ROOTDIR%\firmware\applesoft\build"
IF ERRORLEVEL 1 GOTO :EOF

RENAME applesoft     apple2plus_A$D000_L$2800_applesoft
RENAME applesoft.map apple2plus_A$D000_L$2800_applesoft.map

CALL "%ROOTDIR%\firmware\monitor\build 2"
IF ERRORLEVEL 1 GOTO :EOF

RENAME monitor     apple2plus_A$F800_L$0800_monitor
RENAME monitor.map apple2plus_A$F800_L$0800_monitor.map

CD ..





MKDIR disk2_13sector
CD disk2_13sector

CALL "%ROOTDIR%\firmware\disk2\build" 13
IF ERRORLEVEL 1 GOTO :EOF

RENAME slot6     disk2_A$C600_L$0100_13sector
RENAME slot6.map disk2_A$C600_L$0100_13sector.map

CD ..





MKDIR disk2_16sector
CD disk2_16sector

CALL "%ROOTDIR%\firmware\disk2\build" 16
IF ERRORLEVEL 1 GOTO :EOF

RENAME slot6     disk2_A$C600_L$0100_16sector
RENAME slot6.map disk2_A$C600_L$0100_16sector.map

CD ..





ENDLOCAL
GOTO :EOF
