INCLUDES = -I/usr/include/qt4
METASOURCES = AUTO
bin_PROGRAMS = epple2
epple2_MOC = screen.moc.cc screenimage.moc.cc timinggenerator.moc.cc gui.moc.cc monitorcontrolpanel.moc.cc contentpane.moc.cc
epple2_SOURCES = a2colorsobserved.cpp a2colorsobserved.h addressbus.cpp \
	addressbus.h analogtv.cpp analogtv.h apple2.cpp apple2.h applentsc.cpp applentsc.h \
	card.cpp card.h cpu.cpp cpu.h diskbytes.cpp diskbytes.h diskcontroller.cpp \
	diskcontroller.h drive.cpp drive.h emptyslot.cpp emptyslot.h emulator.cpp emulator.h \
	firmwarecard.cpp firmwarecard.h gui.cpp guiemulator.cpp guiemulator.h gui.h keyboard.cpp \
	keyboard.h languagecard.cpp languagecard.h lowpass_1_5_mhz.cpp lowpass_1_5_mhz.h \
	lowpass_3_58_mhz.cpp lowpass_3_58_mhz.h main.cpp memory.cpp memory.h paddlebuttonstates.cpp \
	paddlebuttonstates.h paddles.cpp paddles.h picturegenerator.cpp picturegenerator.h \
	powerupreset.cpp powerupreset.h raminitializer.cpp raminitializer.h screen.cpp screen.h \
	screenimage.cpp screenimage.h slots.cpp slots.h speakerclicker.cpp speakerclicker.h \
	steppermotor.cpp steppermotor.h textcharacters.cpp textcharacters.h throttle.cpp \
	throttle.h timable.cpp timable.h timinggenerator.cpp timinggenerator.h util.h \
	videoaddressing.cpp videoaddressing.h video.cpp video.h videomode.cpp videomode.h \
	videostaticgenerator.cpp videostaticgenerator.h $(epple2_MOC) monitorcontrolpanel.cpp \
	contentpane.cpp
BUILT_SOURCES = $(epple2_MOC)
epple2_LDADD = -lQtOpenGL -lGLU -lQtGui -lQtCore

%.moc.cc: %.h
	$(MOC) -o $@ $<
noinst_HEADERS = monitorcontrolpanel.h contentpane.h
