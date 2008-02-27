# Microsoft Developer Studio Project File - Name="epple2" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) External Target" 0x0106

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
!MESSAGE "epple2 - Win32 Release" (based on "Win32 (x86) External Target")
!MESSAGE "epple2 - Win32 Debug" (based on "Win32 (x86) External Target")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""

!IF  "$(CFG)" == "epple2 - Win32 Release"

# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Cmd_Line "NMAKE /f Makefile.msvc release"
# PROP BASE Rebuild_Opt "/a"
# PROP BASE Target_File "release\epple2.exe"
# PROP BASE Bsc_Name "epple2.bsc"
# PROP BASE Target_Dir ""
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Cmd_Line "NMAKE /f Makefile.msvc release"
# PROP Rebuild_Opt "/a"
# PROP Target_File "release\epple2.exe"
# PROP Bsc_Name "epple2.bsc"
# PROP Target_Dir ""

!ELSEIF  "$(CFG)" == "epple2 - Win32 Debug"

# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Cmd_Line "NMAKE /f Makefile.msvc debug"
# PROP BASE Rebuild_Opt "/a"
# PROP BASE Target_File "debug\epple2.exe"
# PROP BASE Bsc_Name "epple2.bsc"
# PROP BASE Target_Dir ""
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Cmd_Line "NMAKE /f Makefile.msvc debug"
# PROP Rebuild_Opt "/a"
# PROP Target_File "debug\epple2.exe"
# PROP Bsc_Name "epple2.bsc"
# PROP Target_Dir ""

!ENDIF 

# Begin Target

# Name "epple2 - Win32 Release"
# Name "epple2 - Win32 Debug"

!IF  "$(CFG)" == "epple2 - Win32 Release"

!ELSEIF  "$(CFG)" == "epple2 - Win32 Debug"

!ENDIF 

# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Source File

SOURCE=.\src\a2colorsobserved.cpp
# End Source File
# Begin Source File

SOURCE=.\src\addressbus.cpp
# End Source File
# Begin Source File

SOURCE=.\src\analogtv.cpp
# End Source File
# Begin Source File

SOURCE=.\src\apple2.cpp
# End Source File
# Begin Source File

SOURCE=.\src\applentsc.cpp
# End Source File
# Begin Source File

SOURCE=.\src\card.cpp
# End Source File
# Begin Source File

SOURCE=.\src\contentpane.cpp
# End Source File
# Begin Source File

SOURCE=.\src\cpu.cpp
# End Source File
# Begin Source File

SOURCE=.\src\diskbytes.cpp
# End Source File
# Begin Source File

SOURCE=.\src\diskcontroller.cpp
# End Source File
# Begin Source File

SOURCE=.\src\drive.cpp
# End Source File
# Begin Source File

SOURCE=.\src\emptyslot.cpp
# End Source File
# Begin Source File

SOURCE=.\src\emulator.cpp
# End Source File
# Begin Source File

SOURCE=.\src\firmwarecard.cpp
# End Source File
# Begin Source File

SOURCE=.\src\gui.cpp
# End Source File
# Begin Source File

SOURCE=.\src\guiemulator.cpp
# End Source File
# Begin Source File

SOURCE=.\src\Keyboard.cpp
# End Source File
# Begin Source File

SOURCE=.\src\languagecard.cpp
# End Source File
# Begin Source File

SOURCE=.\src\lowpass_1_5_mhz.cpp
# End Source File
# Begin Source File

SOURCE=.\src\lowpass_3_58_mhz.cpp
# End Source File
# Begin Source File

SOURCE=.\src\main.cpp
# End Source File
# Begin Source File

SOURCE=.\src\memory.cpp
# End Source File
# Begin Source File

SOURCE=.\src\moc_playqmake.cpp
# End Source File
# Begin Source File

SOURCE=.\src\monitorcontrolpanel.cpp
# End Source File
# Begin Source File

SOURCE=.\src\paddlebuttonstates.cpp
# End Source File
# Begin Source File

SOURCE=.\src\paddles.cpp
# End Source File
# Begin Source File

SOURCE=.\src\picturegenerator.cpp
# End Source File
# Begin Source File

SOURCE=.\src\playqmake.cpp
# End Source File
# Begin Source File

SOURCE=.\src\powerupreset.cpp
# End Source File
# Begin Source File

SOURCE=.\src\RAMInitializer.cpp
# End Source File
# Begin Source File

SOURCE=.\src\renderthread.cpp
# End Source File
# Begin Source File

SOURCE=.\src\screen.cpp
# End Source File
# Begin Source File

SOURCE=.\src\screenimage.cpp
# End Source File
# Begin Source File

SOURCE=.\src\slots.cpp
# End Source File
# Begin Source File

SOURCE=.\src\speakerclicker.cpp
# End Source File
# Begin Source File

SOURCE=.\src\steppermotor.cpp
# End Source File
# Begin Source File

SOURCE=.\src\textcharacters.cpp
# End Source File
# Begin Source File

SOURCE=.\src\throttle.cpp
# End Source File
# Begin Source File

SOURCE=.\src\timable.cpp
# End Source File
# Begin Source File

SOURCE=.\src\timinggenerator.cpp
# End Source File
# Begin Source File

SOURCE=.\src\video.cpp
# End Source File
# Begin Source File

SOURCE=.\src\videoaddressing.cpp
# End Source File
# Begin Source File

SOURCE=.\src\videomode.cpp
# End Source File
# Begin Source File

SOURCE=.\src\videostaticgenerator.cpp
# End Source File
# End Group
# Begin Group "Header Files"

# PROP Default_Filter "h;hpp;hxx;hm;inl"
# Begin Source File

SOURCE=.\src\a2colorindex.h
# End Source File
# Begin Source File

SOURCE=.\src\a2colorsobserved.h
# End Source File
# Begin Source File

SOURCE=.\src\addressbus.h
# End Source File
# Begin Source File

SOURCE=.\src\analogtv.h
# End Source File
# Begin Source File

SOURCE=.\src\apple2.h
# End Source File
# Begin Source File

SOURCE=.\src\applentsc.h
# End Source File
# Begin Source File

SOURCE=.\src\card.h
# End Source File
# Begin Source File

SOURCE=.\src\contentpane.h
# End Source File
# Begin Source File

SOURCE=.\src\cpu.h
# End Source File
# Begin Source File

SOURCE=.\src\diskbytes.h
# End Source File
# Begin Source File

SOURCE=.\src\diskcontroller.h
# End Source File
# Begin Source File

SOURCE=.\src\displaytype.h
# End Source File
# Begin Source File

SOURCE=.\src\drive.h
# End Source File
# Begin Source File

SOURCE=.\src\emptyslot.h
# End Source File
# Begin Source File

SOURCE=.\src\emulator.h
# End Source File
# Begin Source File

SOURCE=.\src\firmwarecard.h
# End Source File
# Begin Source File

SOURCE=.\src\gui.h
# End Source File
# Begin Source File

SOURCE=.\src\guiemulator.h
# End Source File
# Begin Source File

SOURCE=.\src\Keyboard.h
# End Source File
# Begin Source File

SOURCE=.\src\languagecard.h
# End Source File
# Begin Source File

SOURCE=.\src\lowpass_1_5_mhz.h
# End Source File
# Begin Source File

SOURCE=.\src\lowpass_3_58_mhz.h
# End Source File
# Begin Source File

SOURCE=.\src\memory.h
# End Source File
# Begin Source File

SOURCE=.\src\monitorcontrolpanel.h
# End Source File
# Begin Source File

SOURCE=.\src\paddlebuttonstates.h
# End Source File
# Begin Source File

SOURCE=.\src\paddles.h
# End Source File
# Begin Source File

SOURCE=.\src\picturegenerator.h
# End Source File
# Begin Source File

SOURCE=.\src\playqmake.h
# End Source File
# Begin Source File

SOURCE=.\src\powerupreset.h
# End Source File
# Begin Source File

SOURCE=.\src\RAMInitializer.h
# End Source File
# Begin Source File

SOURCE=.\src\renderthread.h
# End Source File
# Begin Source File

SOURCE=.\src\screen.h
# End Source File
# Begin Source File

SOURCE=.\src\screenimage.h
# End Source File
# Begin Source File

SOURCE=.\src\slots.h
# End Source File
# Begin Source File

SOURCE=.\src\speakerclicker.h
# End Source File
# Begin Source File

SOURCE=.\src\steppermotor.h
# End Source File
# Begin Source File

SOURCE=.\src\textcharacterimages.h
# End Source File
# Begin Source File

SOURCE=.\src\textcharacters.h
# End Source File
# Begin Source File

SOURCE=.\src\throttle.h
# End Source File
# Begin Source File

SOURCE=.\src\timable.h
# End Source File
# Begin Source File

SOURCE=.\src\timinggenerator.h
# End Source File
# Begin Source File

SOURCE=.\src\util.h
# End Source File
# Begin Source File

SOURCE=.\src\video.h
# End Source File
# Begin Source File

SOURCE=.\src\videoaddressing.h
# End Source File
# Begin Source File

SOURCE=.\src\videomode.h
# End Source File
# Begin Source File

SOURCE=.\src\videostaticgenerator.h
# End Source File
# End Group
# End Target
# End Project
