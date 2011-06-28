# Microsoft Developer Studio Project File - Name="selfextr" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Application" 0x0101

CFG=selfextr - Win32 Release
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "selfextr.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "selfextr.mak" CFG="selfextr - Win32 Release"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "selfextr - Win32 Release" (based on "Win32 (x86) Application")
!MESSAGE "selfextr - Win32 Release NT" (based on "Win32 (x86) Application")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""$/GRO/selfextr", OFDAAAAA"
# PROP Scc_LocalPath "."
CPP=cl.exe
MTL=midl.exe
RSC=rc.exe

!IF  "$(CFG)" == "selfextr - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "..\Distribution"
# PROP Intermediate_Dir "Release"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /YX /FD /c
# ADD CPP /nologo /MD /W3 /GX /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "COMPAT98" /YX /FD /c
# ADD BASE MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /d "NDEBUG" /d "COMPAT98"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:windows /machine:I386
# ADD LINK32 msvcrt.lib shell32.lib kernel32.lib user32.lib /nologo /subsystem:windows /machine:I386 /nodefaultlib /out:"..\Distribution\V140/gro98v140.exe"
# SUBTRACT LINK32 /pdb:none

!ELSEIF  "$(CFG)" == "selfextr - Win32 Release NT"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "selfextr___Win32_Release_NT"
# PROP BASE Intermediate_Dir "selfextr___Win32_Release_NT"
# PROP BASE Ignore_Export_Lib 0
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "selfextr___Win32_Release_NT"
# PROP Intermediate_Dir "selfextr___Win32_Release_NT"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MD /W3 /GX /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /YX /FD /c
# ADD CPP /nologo /MD /W3 /GX /O1 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /YX /FD /c
# ADD BASE MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 msvcrt.lib shell32.lib kernel32.lib /nologo /subsystem:windows /machine:I386 /nodefaultlib /out:"..\Distribution\V140/grov140.exe"
# SUBTRACT BASE LINK32 /pdb:none
# ADD LINK32 msvcrt.lib shell32.lib kernel32.lib /nologo /subsystem:windows /machine:I386 /nodefaultlib /out:"..\Distribution\V140/grov140.exe"
# SUBTRACT LINK32 /pdb:none

!ENDIF 

# Begin Target

# Name "selfextr - Win32 Release"
# Name "selfextr - Win32 Release NT"
# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Source File

SOURCE=.\selfextr.cpp
# End Source File
# Begin Source File

SOURCE=.\selfextr.rc
# End Source File
# End Group
# Begin Group "Header Files"

# PROP Default_Filter "h;hpp;hxx;hm;inl"
# Begin Source File

SOURCE=.\bzlib.h
# End Source File
# Begin Source File

SOURCE=.\resource.h
# End Source File
# End Group
# Begin Group "Resource Files"

# PROP Default_Filter "ico;cur;bmp;dlg;rc2;rct;bin;rgs;gif;jpg;jpeg;jpe"
# Begin Source File

SOURCE=.\package.ico
# End Source File
# Begin Source File

SOURCE=.\Res\selfextr.rc2
# End Source File
# End Group
# Begin Group "Zip Source Files"

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\Release\Gro.exe

!IF  "$(CFG)" == "selfextr - Win32 Release"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\Release\Gro.exe

"encl.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) encl.bz 
	minibz2 $(InputPath) encl.bz 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "selfextr - Win32 Release NT"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\Release\Gro.exe

"encl.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) encl.bz 
	minibz2 $(InputPath) encl.bz 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=..\Release_NT\Gro.exe

!IF  "$(CFG)" == "selfextr - Win32 Release"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\Release_NT\Gro.exe

"enclnt.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) enclnt.bz 
	minibz2 $(InputPath) enclnt.bz 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "selfextr - Win32 Release NT"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\Release_NT\Gro.exe

"enclnt.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) enclnt.bz 
	minibz2 $(InputPath) enclnt.bz 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=..\groaplt.jar

!IF  "$(CFG)" == "selfextr - Win32 Release"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\groaplt.jar

"encl2.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) encl2.bz 
	minibz2 $(InputPath) encl2.bz 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "selfextr - Win32 Release NT"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\groaplt.jar

"encl2.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) encl2.bz 
	minibz2 $(InputPath) encl2.bz 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=..\Release\MFC42LU.DLL

!IF  "$(CFG)" == "selfextr - Win32 Release"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\Release\MFC42LU.DLL

"encl4.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) encl4.bz 
	minibz2 $(InputPath) encl4.bz 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "selfextr - Win32 Release NT"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\Release\MFC42LU.DLL

"encl4.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) encl4.bz 
	minibz2 $(InputPath) encl4.bz 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=..\Release\MSLUIRT.dll

!IF  "$(CFG)" == "selfextr - Win32 Release"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\Release\MSLUIRT.dll

"encl5.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) encl5.bz 
	minibz2 $(InputPath) encl5.bz 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "selfextr - Win32 Release NT"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\Release\MSLUIRT.dll

"encl5.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) encl5.bz 
	minibz2 $(InputPath) encl5.bz 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=..\Release\MSLUP60.dll

!IF  "$(CFG)" == "selfextr - Win32 Release"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\Release\MSLUP60.dll

"encl6.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) encl6.bz 
	minibz2 $(InputPath) encl6.bz 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "selfextr - Win32 Release NT"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\Release\MSLUP60.dll

"encl6.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) encl6.bz 
	minibz2 $(InputPath) encl6.bz 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=..\Release\MSLURT.dll

!IF  "$(CFG)" == "selfextr - Win32 Release"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\Release\MSLURT.dll

"encl7.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) encl7.bz 
	minibz2 $(InputPath) encl7.bz 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "selfextr - Win32 Release NT"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\Release\MSLURT.dll

"encl7.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) encl7.bz 
	minibz2 $(InputPath) encl7.bz 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=..\unicows.dll

!IF  "$(CFG)" == "selfextr - Win32 Release"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\unicows.dll

"encl3.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) encl3.bz 
	minibz2 $(InputPath) encl3.bz 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "selfextr - Win32 Release NT"

# Begin Custom Build - Compressing $(InputPath)
InputPath=..\unicows.dll

"encl3.bz" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	echo minibz2 $(InputPath) encl3.bz 
	minibz2 $(InputPath) encl3.bz 
	
# End Custom Build

!ENDIF 

# End Source File
# End Group
# Begin Source File

SOURCE=.\bzipslib.lib
# End Source File
# End Target
# End Project
