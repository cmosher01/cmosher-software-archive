# Microsoft Developer Studio Project File - Name="epple2" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Console Application" 0x0103

CFG=epple2 - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "epple2.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "epple2.mak" CFG="epple2 - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "epple2 - Win32 Release" (based on "Win32 (x86) Console Application")
!MESSAGE "epple2 - Win32 Debug" (based on "Win32 (x86) Console Application")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
RSC=rc.exe

!IF  "$(CFG)" == "epple2 - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /Zp1 /MTd /Za /W3 /GR /GX /Zi /O2 /D "NDEBUG" /FR /FD /c
# ADD CPP /Zp1 /MTd /Ze /W3 /GR /GX /Zi /O2 /D "NDEBUG" /FR /FD /c
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /d "NDEBUG"
BSC32=bscmake.exe
LINK32=link.exe
# ADD BASE LINK32 libcmtd.lib libcpmtd.lib libcimtd.lib kernel32.lib /subsystem:console /debug /machine:I386 /nodefaultlib /pdbtype:sept
# ADD LINK32 libcmtd.lib libcpmtd.lib libcimtd.lib kernel32.lib /subsystem:console /debug /machine:I386 /nodefaultlib /pdbtype:sept
# Begin Special Build Tool
TargetPath=.\Release\epple2.exe
SOURCE="$(InputPath)"
PostBuild_Cmds=$(TargetPath)
# End Special Build Tool

!ELSEIF  "$(CFG)" == "epple2 - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /Zp1 /MTd /Za /W3 /GR /GX /Zi /O2 /FR /FD /c
# ADD CPP /Zp1 /MTd /Ze /W3 /GR /GX /Zi /O2 /FR /FD /c
# ADD BASE RSC /l 0x409
# ADD RSC /l 0x409
BSC32=bscmake.exe
LINK32=link.exe
# ADD BASE LINK32 libcmtd.lib libcpmtd.lib libcimtd.lib kernel32.lib /subsystem:console /incremental:no /debug /machine:I386 /nodefaultlib /pdbtype:sept
# ADD LINK32 libcmtd.lib libcpmtd.lib libcimtd.lib kernel32.lib /subsystem:console /incremental:no /debug /machine:I386 /nodefaultlib /pdbtype:sept
# Begin Special Build Tool
TargetPath=.\Debug\epple2.exe
SOURCE="$(InputPath)"
PostBuild_Cmds=$(TargetPath)
# End Special Build Tool

!ENDIF 

# Begin Target

# Name "epple2 - Win32 Release"
# Name "epple2 - Win32 Debug"
# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Source File

SOURCE=.\src\addressbus.cpp
# End Source File
# Begin Source File

SOURCE=.\src\card.cpp
# End Source File
# Begin Source File

SOURCE=.\src\emptyslot.cpp
# End Source File
# Begin Source File

SOURCE=.\src\Keyboard.cpp
# End Source File
# Begin Source File

SOURCE=.\src\main.cpp
# End Source File
# Begin Source File

SOURCE=.\src\memory.cpp
# End Source File
# Begin Source File

SOURCE=.\src\playqmake.cpp
# End Source File
# Begin Source File

SOURCE=.\src\RAMInitializer.cpp
# End Source File
# Begin Source File

SOURCE=.\src\slots.cpp
# End Source File
# Begin Source File

SOURCE=.\src\timinggenerator.cpp
# End Source File
# Begin Source File

SOURCE=.\src\videoaddressing.cpp
# End Source File
# Begin Source File

SOURCE=.\src\videomode.cpp
# End Source File
# End Group
# Begin Group "Header Files"

# PROP Default_Filter "h;hpp;hxx;hm;inl"
# Begin Source File

SOURCE=.\src\addressbus.h
# End Source File
# Begin Source File

SOURCE=.\src\card.h
# End Source File
# Begin Source File

SOURCE=.\src\emptyslot.h
# End Source File
# Begin Source File

SOURCE=.\src\Keyboard.h
# End Source File
# Begin Source File

SOURCE=.\src\memory.h
# End Source File
# Begin Source File

SOURCE=.\src\playqmake.h
# End Source File
# Begin Source File

SOURCE=.\src\RAMInitializer.h
# End Source File
# Begin Source File

SOURCE=.\src\slots.h
# End Source File
# Begin Source File

SOURCE=.\src\timinggenerator.h
# End Source File
# Begin Source File

SOURCE=.\src\util.h
# End Source File
# Begin Source File

SOURCE=.\src\videoaddressing.h
# End Source File
# Begin Source File

SOURCE=.\src\videomode.h
# End Source File
# End Group
# Begin Group "Resource Files"

# PROP Default_Filter "ico;cur;bmp;dlg;rc2;rct;bin;rgs;gif;jpg;jpeg;jpe"
# End Group
# End Target
# End Project
