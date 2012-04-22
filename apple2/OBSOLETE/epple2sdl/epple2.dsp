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
# ADD BASE CPP /MD /W3 /GX /Zi /O2 /I "src" /I "\sdl\sdl-1.2.13\include" /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /FD /c
# ADD CPP /MD /W2 /GX /Zi /O2 /I "src" /I "\sdl\sdl-1.2.13\include" /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /FR /FD /c
# SUBTRACT CPP /WX
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib msvcrt.lib msvcprt.lib \sdl\sdl-1.2.13\lib\SDL.lib \sdl\sdl-1.2.13\lib\SDLmain.lib /nologo /subsystem:console /machine:I386 /nodefaultlib /force
# ADD LINK32 msvcrt.lib msvcprt.lib kernel32.lib user32.lib gdi32.lib \sdl\sdl-1.2.13\lib\SDL.lib \sdl\sdl-1.2.13\lib\SDLmain.lib /nologo /subsystem:console /profile /machine:I386 /nodefaultlib /force

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
# ADD BASE CPP /MDd /W3 /GX /Zi /O2 /I "src" /I "\sdl\sdl-1.2.13\include" /D "WIN32" /D "_CONSOLE" /D "_MBCS" /FR /FD /c
# ADD CPP /MDd /W2 /GX /Zi /O2 /I "src" /I "\sdl\sdl-1.2.13\include" /D "WIN32" /D "_CONSOLE" /D "_MBCS" /FR /FD /c
# SUBTRACT CPP /WX
# ADD BASE RSC /l 0x409 /d "_DEBUG"
# ADD RSC /l 0x409 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib msvcrtd.lib msvcprtd.lib \sdl\sdl-1.2.13\lib\SDL.lib \sdl\sdl-1.2.13\lib\SDLmain.lib /nologo /subsystem:console /debug /machine:I386 /nodefaultlib /force /pdbtype:sept
# ADD LINK32 msvcrtd.lib msvcprtd.lib kernel32.lib user32.lib gdi32.lib \sdl\sdl-1.2.13\lib\SDL.lib \sdl\sdl-1.2.13\lib\SDLmain.lib /nologo /subsystem:console /profile /debug /machine:I386 /nodefaultlib /force

!ENDIF 

# Begin Target

# Name "epple2 - Win32 Release"
# Name "epple2 - Win32 Debug"
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

SOURCE=.\src\clipboardhandler.cpp
# End Source File
# Begin Source File

SOURCE=.\src\clockcard.cpp
# End Source File
# Begin Source File

SOURCE=.\src\configep2.cpp
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

SOURCE=.\src\hypermode.cpp
# End Source File
# Begin Source File

SOURCE=.\src\keyboard.cpp
# End Source File
# Begin Source File

SOURCE=.\src\keyboardbuffermode.cpp
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

SOURCE=.\src\paddlebuttonstates.cpp
# End Source File
# Begin Source File

SOURCE=.\src\paddles.cpp
# End Source File
# Begin Source File

SOURCE=.\src\picturegenerator.cpp
# End Source File
# Begin Source File

SOURCE=.\src\powerupreset.cpp
# End Source File
# Begin Source File

SOURCE=.\src\raminitializer.cpp
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

SOURCE=.\src\standardin.cpp
# End Source File
# Begin Source File

SOURCE=.\src\standardinproducer.cpp
# End Source File
# Begin Source File

SOURCE=.\src\standardout.cpp
# End Source File
# Begin Source File

SOURCE=.\src\steppermotor.cpp
# End Source File
# Begin Source File

SOURCE=.\src\textcharacters.cpp
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

SOURCE=.\src\clipboardhandler.h
# End Source File
# Begin Source File

SOURCE=.\src\clockcard.h
# End Source File
# Begin Source File

SOURCE=.\src\configep2.h
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

SOURCE=.\src\font3x5.h
# End Source File
# Begin Source File

SOURCE=.\src\hypermode.h
# End Source File
# Begin Source File

SOURCE=.\src\keyboard.h
# End Source File
# Begin Source File

SOURCE=.\src\keyboardbuffermode.h
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

SOURCE=.\src\paddlebuttonstates.h
# End Source File
# Begin Source File

SOURCE=.\src\paddles.h
# End Source File
# Begin Source File

SOURCE=.\src\picturegenerator.h
# End Source File
# Begin Source File

SOURCE=.\src\powerupreset.h
# End Source File
# Begin Source File

SOURCE=.\src\raminitializer.h
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

SOURCE=.\src\standardin.h
# End Source File
# Begin Source File

SOURCE=.\src\standardinproducer.h
# End Source File
# Begin Source File

SOURCE=.\src\standardout.h
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
# Begin Group "Resource Files"

# PROP Default_Filter "ico;cur;bmp;dlg;rc2;rct;bin;rgs;gif;jpg;jpeg;jpe"
# End Group
# End Target
# End Project
