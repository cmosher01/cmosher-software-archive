#############################################################################
# Makefile for building: playqmake
# Generated by qmake (2.01a) (Qt 4.2.1) on: Tue Feb 19 20:43:30 2008
# Project:  playqmake.pro
# Template: app
# Command: /usr/bin/qmake -unix -o Makefile playqmake.pro
#############################################################################

####### Compiler, tools and options

CC            = gcc
CXX           = g++
LEX           = flex
YACC          = yacc
DEFINES       = -DQT_NO_DEBUG -DQT_GUI_LIB -DQT_CORE_LIB
CFLAGS        = -pipe -g -O3 -Wall -W -D_REENTRANT  $(DEFINES)
CXXFLAGS      = -pipe -g -O3 -Wall -W -D_REENTRANT  $(DEFINES)
LEXFLAGS      = 
YACCFLAGS     = -d
INCPATH       = -I/usr/share/qt4/mkspecs/linux-g++ -I/usr/include/qt4/QtCore -I/usr/include/qt4/QtGui -I/usr/include/qt4 -I. -Isrc
LINK          = g++
LFLAGS        = 
LIBS          = $(SUBLIBS)  -L/usr/lib -lQtGui -lQtCore -lpthread -lGLU -lQtOpenGL
AR            = ar cqs
RANLIB        = 
QMAKE         = /usr/bin/qmake
TAR           = tar -cf
COMPRESS      = gzip -9f
COPY          = cp -f
COPY_FILE     = $(COPY)
COPY_DIR      = $(COPY) -r
INSTALL_FILE  = install -m 644 -p
INSTALL_DIR   = $(COPY_DIR)
INSTALL_PROGRAM = install -m 755 -p
DEL_FILE      = rm -f
SYMLINK       = ln -sf
DEL_DIR       = rmdir
MOVE          = mv -f
CHK_DIR_EXISTS= test -d
MKDIR         = mkdir -p

####### Output directory

OBJECTS_DIR   = ./

####### Files

SOURCES       = src/a2colorsobserved.cpp \
		src/addressbus.cpp \
		src/analogtv.cpp \
		src/apple2.cpp \
		src/applentsc.cpp \
		src/card.cpp \
		src/cpu.cpp \
		src/diskbytes.cpp \
		src/diskcontroller.cpp \
		src/drive.cpp \
		src/emptyslot.cpp \
		src/emulator.cpp \
		src/gui.cpp \
		src/guiemulator.cpp \
		src/Keyboard.cpp \
		src/lowpass_1_5_mhz.cpp \
		src/lowpass_3_58_mhz.cpp \
		src/main.cpp \
		src/memory.cpp \
		src/paddlebuttonstates.cpp \
		src/paddles.cpp \
		src/picturegenerator.cpp \
		src/powerupreset.cpp \
		src/RAMInitializer.cpp \
		src/screen.cpp \
		src/screenimage.cpp \
		src/slots.cpp \
		src/speakerclicker.cpp \
		src/steppermotor.cpp \
		src/textcharacters.cpp \
		src/throttle.cpp \
		src/timable.cpp \
		src/timinggenerator.cpp \
		src/video.cpp \
		src/videoaddressing.cpp \
		src/videodisplaydevice.cpp \
		src/videomode.cpp \
		src/videostaticgenerator.cpp moc_screen.cpp \
		moc_screenimage.cpp \
		moc_timinggenerator.cpp
OBJECTS       = a2colorsobserved.o \
		addressbus.o \
		analogtv.o \
		apple2.o \
		applentsc.o \
		card.o \
		cpu.o \
		diskbytes.o \
		diskcontroller.o \
		drive.o \
		emptyslot.o \
		emulator.o \
		gui.o \
		guiemulator.o \
		Keyboard.o \
		lowpass_1_5_mhz.o \
		lowpass_3_58_mhz.o \
		main.o \
		memory.o \
		paddlebuttonstates.o \
		paddles.o \
		picturegenerator.o \
		powerupreset.o \
		RAMInitializer.o \
		screen.o \
		screenimage.o \
		slots.o \
		speakerclicker.o \
		steppermotor.o \
		textcharacters.o \
		throttle.o \
		timable.o \
		timinggenerator.o \
		video.o \
		videoaddressing.o \
		videodisplaydevice.o \
		videomode.o \
		videostaticgenerator.o \
		moc_screen.o \
		moc_screenimage.o \
		moc_timinggenerator.o
DIST          = /usr/share/qt4/mkspecs/common/unix.conf \
		/usr/share/qt4/mkspecs/common/g++.conf \
		/usr/share/qt4/mkspecs/common/linux.conf \
		/usr/share/qt4/mkspecs/qconfig.pri \
		/usr/share/qt4/mkspecs/features/qt_functions.prf \
		/usr/share/qt4/mkspecs/features/qt_config.prf \
		/usr/share/qt4/mkspecs/features/exclusive_builds.prf \
		/usr/share/qt4/mkspecs/features/default_pre.prf \
		/usr/share/qt4/mkspecs/features/release.prf \
		/usr/share/qt4/mkspecs/features/default_post.prf \
		/usr/share/qt4/mkspecs/features/warn_on.prf \
		/usr/share/qt4/mkspecs/features/qt.prf \
		/usr/share/qt4/mkspecs/features/unix/thread.prf \
		/usr/share/qt4/mkspecs/features/moc.prf \
		/usr/share/qt4/mkspecs/features/resources.prf \
		/usr/share/qt4/mkspecs/features/uic.prf \
		playqmake.pro
QMAKE_TARGET  = playqmake
DESTDIR       = 
TARGET        = playqmake

first: all
####### Implicit rules

.SUFFIXES: .o .c .cpp .cc .cxx .C

.cpp.o:
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o "$@" "$<"

.cc.o:
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o "$@" "$<"

.cxx.o:
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o "$@" "$<"

.C.o:
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o "$@" "$<"

.c.o:
	$(CC) -c $(CFLAGS) $(INCPATH) -o "$@" "$<"

####### Build rules

all: $(TARGET)

$(TARGET):  $(OBJECTS)  
	$(LINK) $(LFLAGS) -o $(TARGET) $(OBJECTS) $(OBJCOMP) $(LIBS)

dist: 
	@$(CHK_DIR_EXISTS) .tmp/playqmake1.0.0 || $(MKDIR) .tmp/playqmake1.0.0 
	$(COPY_FILE) --parents $(SOURCES) $(DIST) .tmp/playqmake1.0.0/ && $(COPY_FILE) --parents src/a2colorsobserved.h src/addressbus.h src/analogtv.h src/apple2.h src/applentsc.h src/card.h src/cpu.h src/diskbytes.h src/diskcontroller.h src/drive.h src/emptyslot.h src/emulator.h src/gui.h src/guiemulator.h src/Keyboard.h src/lowpass_1_5_mhz.h src/lowpass_3_58_mhz.h src/memory.h src/paddlebuttonstates.h src/paddles.h src/picturegenerator.h src/powerupreset.h src/RAMInitializer.h src/screen.h src/screenimage.h src/slots.h src/speakerclicker.h src/steppermotor.h src/textcharacters.h src/throttle.h src/timable.h src/timinggenerator.h src/util.h src/video.h src/videoaddressing.h src/videodisplaydevice.h src/videomode.h src/videostaticgenerator.h .tmp/playqmake1.0.0/ && $(COPY_FILE) --parents src/a2colorsobserved.cpp src/addressbus.cpp src/analogtv.cpp src/apple2.cpp src/applentsc.cpp src/card.cpp src/cpu.cpp src/diskbytes.cpp src/diskcontroller.cpp src/drive.cpp src/emptyslot.cpp src/emulator.cpp src/gui.cpp src/guiemulator.cpp src/Keyboard.cpp src/lowpass_1_5_mhz.cpp src/lowpass_3_58_mhz.cpp src/main.cpp src/memory.cpp src/paddlebuttonstates.cpp src/paddles.cpp src/picturegenerator.cpp src/powerupreset.cpp src/RAMInitializer.cpp src/screen.cpp src/screenimage.cpp src/slots.cpp src/speakerclicker.cpp src/steppermotor.cpp src/textcharacters.cpp src/throttle.cpp src/timable.cpp src/timinggenerator.cpp src/video.cpp src/videoaddressing.cpp src/videodisplaydevice.cpp src/videomode.cpp src/videostaticgenerator.cpp .tmp/playqmake1.0.0/ && (cd `dirname .tmp/playqmake1.0.0` && $(TAR) playqmake1.0.0.tar playqmake1.0.0 && $(COMPRESS) playqmake1.0.0.tar) && $(MOVE) `dirname .tmp/playqmake1.0.0`/playqmake1.0.0.tar.gz . && $(DEL_FILE) -r .tmp/playqmake1.0.0


yaccclean:
lexclean:
clean:compiler_clean 
	-$(DEL_FILE) $(OBJECTS)
	-$(DEL_FILE) *~ core *.core


####### Sub-libraries

distclean: clean
	-$(DEL_FILE) $(TARGET) 
	-$(DEL_FILE) Makefile


/usr/bin/moc-qt4:
	(cd $(QTDIR)/src/tools/moc && $(MAKE))

mocclean: compiler_moc_header_clean compiler_moc_source_clean

mocables: compiler_moc_header_make_all compiler_moc_source_make_all

compiler_moc_header_make_all: moc_screen.cpp moc_screenimage.cpp moc_timinggenerator.cpp
compiler_moc_header_clean:
	-$(DEL_FILE) moc_screen.cpp moc_screenimage.cpp moc_timinggenerator.cpp
moc_screen.cpp: src/Keyboard.h \
		src/screen.h \
		/usr/bin/moc-qt4
	/usr/bin/moc-qt4 $(DEFINES) $(INCPATH) src/screen.h -o moc_screen.cpp

moc_screenimage.cpp: src/applentsc.h \
		src/videoaddressing.h \
		src/screenimage.h \
		/usr/bin/moc-qt4
	/usr/bin/moc-qt4 $(DEFINES) $(INCPATH) src/screenimage.h -o moc_screenimage.cpp

moc_timinggenerator.cpp: src/timinggenerator.h \
		/usr/bin/moc-qt4
	/usr/bin/moc-qt4 $(DEFINES) $(INCPATH) src/timinggenerator.h -o moc_timinggenerator.cpp

compiler_rcc_make_all:
compiler_rcc_clean:
compiler_image_collection_make_all: qmake_image_collection.cpp
compiler_image_collection_clean:
	-$(DEL_FILE) qmake_image_collection.cpp
compiler_moc_source_make_all:
compiler_moc_source_clean:
compiler_uic_make_all:
compiler_uic_clean:
compiler_clean: compiler_moc_header_clean compiler_rcc_clean compiler_image_collection_clean compiler_moc_source_clean compiler_uic_clean 

####### Compile

a2colorsobserved.o: src/a2colorsobserved.cpp src/a2colorsobserved.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o a2colorsobserved.o src/a2colorsobserved.cpp

addressbus.o: src/addressbus.cpp src/addressbus.h \
		src/memory.h \
		src/Keyboard.h \
		src/videomode.h \
		src/videoaddressing.h \
		src/paddles.h \
		src/paddlebuttonstates.h \
		src/speakerclicker.h \
		src/slots.h \
		src/card.h \
		src/emptyslot.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o addressbus.o src/addressbus.cpp

analogtv.o: src/analogtv.cpp src/analogtv.h \
		src/applentsc.h \
		src/a2colorsobserved.h \
		src/screenimage.h \
		src/videoaddressing.h \
		src/lowpass_3_58_mhz.h \
		src/lowpass_1_5_mhz.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o analogtv.o src/analogtv.cpp

apple2.o: src/apple2.cpp src/apple2.h \
		src/timable.h \
		src/slots.h \
		src/card.h \
		src/memory.h \
		src/emptyslot.h \
		src/videomode.h \
		src/videoaddressing.h \
		src/Keyboard.h \
		src/addressbus.h \
		src/picturegenerator.h \
		src/textcharacters.h \
		src/video.h \
		src/cpu.h \
		src/paddles.h \
		src/paddlebuttonstates.h \
		src/speakerclicker.h \
		src/analogtv.h \
		src/applentsc.h \
		src/a2colorsobserved.h \
		src/powerupreset.h \
		src/diskcontroller.h \
		src/drive.h \
		src/diskbytes.h \
		src/steppermotor.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o apple2.o src/apple2.cpp

applentsc.o: src/applentsc.cpp src/applentsc.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o applentsc.o src/applentsc.cpp

card.o: src/card.cpp src/card.h \
		src/memory.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o card.o src/card.cpp

cpu.o: src/cpu.cpp src/cpu.h \
		src/addressbus.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o cpu.o src/cpu.cpp

diskbytes.o: src/diskbytes.cpp src/diskbytes.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o diskbytes.o src/diskbytes.cpp

diskcontroller.o: src/diskcontroller.cpp src/diskcontroller.h \
		src/card.h \
		src/memory.h \
		src/drive.h \
		src/diskbytes.h \
		src/steppermotor.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o diskcontroller.o src/diskcontroller.cpp

drive.o: src/drive.cpp src/drive.h \
		src/diskbytes.h \
		src/steppermotor.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o drive.o src/drive.cpp

emptyslot.o: src/emptyslot.cpp src/emptyslot.h \
		src/card.h \
		src/memory.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o emptyslot.o src/emptyslot.cpp

emulator.o: src/emulator.cpp src/emulator.h \
		src/throttle.h \
		src/Keyboard.h \
		src/paddlebuttonstates.h \
		src/apple2.h \
		src/timable.h \
		src/slots.h \
		src/card.h \
		src/memory.h \
		src/emptyslot.h \
		src/videomode.h \
		src/videoaddressing.h \
		src/addressbus.h \
		src/picturegenerator.h \
		src/textcharacters.h \
		src/video.h \
		src/cpu.h \
		src/paddles.h \
		src/speakerclicker.h \
		src/analogtv.h \
		src/applentsc.h \
		src/a2colorsobserved.h \
		src/powerupreset.h \
		src/videostaticgenerator.h \
		src/screenimage.h \
		src/timinggenerator.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o emulator.o src/emulator.cpp

gui.o: src/gui.cpp src/gui.h \
		src/screen.h \
		src/Keyboard.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o gui.o src/gui.cpp

guiemulator.o: src/guiemulator.cpp src/guiemulator.h \
		src/emulator.h \
		src/throttle.h \
		src/Keyboard.h \
		src/paddlebuttonstates.h \
		src/apple2.h \
		src/timable.h \
		src/slots.h \
		src/card.h \
		src/memory.h \
		src/emptyslot.h \
		src/videomode.h \
		src/videoaddressing.h \
		src/addressbus.h \
		src/picturegenerator.h \
		src/textcharacters.h \
		src/video.h \
		src/cpu.h \
		src/paddles.h \
		src/speakerclicker.h \
		src/analogtv.h \
		src/applentsc.h \
		src/a2colorsobserved.h \
		src/powerupreset.h \
		src/videostaticgenerator.h \
		src/screenimage.h \
		src/screen.h \
		src/gui.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o guiemulator.o src/guiemulator.cpp

Keyboard.o: src/Keyboard.cpp src/Keyboard.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o Keyboard.o src/Keyboard.cpp

lowpass_1_5_mhz.o: src/lowpass_1_5_mhz.cpp src/lowpass_1_5_mhz.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o lowpass_1_5_mhz.o src/lowpass_1_5_mhz.cpp

lowpass_3_58_mhz.o: src/lowpass_3_58_mhz.cpp src/lowpass_3_58_mhz.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o lowpass_3_58_mhz.o src/lowpass_3_58_mhz.cpp

main.o: src/main.cpp src/guiemulator.h \
		src/emulator.h \
		src/throttle.h \
		src/Keyboard.h \
		src/paddlebuttonstates.h \
		src/apple2.h \
		src/timable.h \
		src/slots.h \
		src/card.h \
		src/memory.h \
		src/emptyslot.h \
		src/videomode.h \
		src/videoaddressing.h \
		src/addressbus.h \
		src/picturegenerator.h \
		src/textcharacters.h \
		src/video.h \
		src/cpu.h \
		src/paddles.h \
		src/speakerclicker.h \
		src/analogtv.h \
		src/applentsc.h \
		src/a2colorsobserved.h \
		src/powerupreset.h \
		src/videostaticgenerator.h \
		src/screenimage.h \
		src/screen.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o main.o src/main.cpp

memory.o: src/memory.cpp src/memory.h \
		src/RAMInitializer.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o memory.o src/memory.cpp

paddlebuttonstates.o: src/paddlebuttonstates.cpp src/paddlebuttonstates.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o paddlebuttonstates.o src/paddlebuttonstates.cpp

paddles.o: src/paddles.cpp src/paddles.h \
		src/timinggenerator.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o paddles.o src/paddles.cpp

picturegenerator.o: src/picturegenerator.cpp src/picturegenerator.h \
		src/videoaddressing.h \
		src/analogtv.h \
		src/applentsc.h \
		src/a2colorsobserved.h \
		src/videomode.h \
		src/timinggenerator.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o picturegenerator.o src/picturegenerator.cpp

powerupreset.o: src/powerupreset.cpp src/powerupreset.h \
		src/apple2.h \
		src/timable.h \
		src/slots.h \
		src/card.h \
		src/memory.h \
		src/emptyslot.h \
		src/videomode.h \
		src/videoaddressing.h \
		src/Keyboard.h \
		src/addressbus.h \
		src/picturegenerator.h \
		src/textcharacters.h \
		src/video.h \
		src/cpu.h \
		src/paddles.h \
		src/paddlebuttonstates.h \
		src/speakerclicker.h \
		src/analogtv.h \
		src/applentsc.h \
		src/a2colorsobserved.h \
		src/timinggenerator.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o powerupreset.o src/powerupreset.cpp

RAMInitializer.o: src/RAMInitializer.cpp src/RAMInitializer.h \
		src/memory.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o RAMInitializer.o src/RAMInitializer.cpp

screen.o: src/screen.cpp src/screen.h \
		src/Keyboard.h \
		src/screenimage.h \
		src/applentsc.h \
		src/videoaddressing.h \
		src/apple2.h \
		src/timable.h \
		src/slots.h \
		src/card.h \
		src/memory.h \
		src/emptyslot.h \
		src/videomode.h \
		src/addressbus.h \
		src/picturegenerator.h \
		src/textcharacters.h \
		src/video.h \
		src/cpu.h \
		src/paddles.h \
		src/paddlebuttonstates.h \
		src/speakerclicker.h \
		src/analogtv.h \
		src/a2colorsobserved.h \
		src/powerupreset.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o screen.o src/screen.cpp

screenimage.o: src/screenimage.cpp src/screenimage.h \
		src/applentsc.h \
		src/videoaddressing.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o screenimage.o src/screenimage.cpp

slots.o: src/slots.cpp src/slots.h \
		src/card.h \
		src/memory.h \
		src/emptyslot.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o slots.o src/slots.cpp

speakerclicker.o: src/speakerclicker.cpp src/speakerclicker.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o speakerclicker.o src/speakerclicker.cpp

steppermotor.o: src/steppermotor.cpp src/steppermotor.h \
		src/util.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o steppermotor.o src/steppermotor.cpp

textcharacters.o: src/textcharacters.cpp src/textcharacters.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o textcharacters.o src/textcharacters.cpp

throttle.o: src/throttle.cpp src/throttle.h \
		src/timinggenerator.h \
		src/util.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o throttle.o src/throttle.cpp

timable.o: src/timable.cpp src/timable.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o timable.o src/timable.cpp

timinggenerator.o: src/timinggenerator.cpp src/timinggenerator.h \
		src/util.h \
		src/throttle.h \
		src/timable.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o timinggenerator.o src/timinggenerator.cpp

video.o: src/video.cpp src/video.h \
		src/timinggenerator.h \
		src/videoaddressing.h \
		src/videomode.h \
		src/addressbus.h \
		src/picturegenerator.h \
		src/textcharacters.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o video.o src/video.cpp

videoaddressing.o: src/videoaddressing.cpp src/timinggenerator.h \
		src/videoaddressing.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o videoaddressing.o src/videoaddressing.cpp

videodisplaydevice.o: src/videodisplaydevice.cpp src/videodisplaydevice.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o videodisplaydevice.o src/videodisplaydevice.cpp

videomode.o: src/videomode.cpp src/videomode.h \
		src/videoaddressing.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o videomode.o src/videomode.cpp

videostaticgenerator.o: src/videostaticgenerator.cpp src/videostaticgenerator.h \
		src/timable.h \
		src/analogtv.h \
		src/applentsc.h \
		src/a2colorsobserved.h \
		src/timinggenerator.h
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o videostaticgenerator.o src/videostaticgenerator.cpp

moc_screen.o: moc_screen.cpp 
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o moc_screen.o moc_screen.cpp

moc_screenimage.o: moc_screenimage.cpp 
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o moc_screenimage.o moc_screenimage.cpp

moc_timinggenerator.o: moc_timinggenerator.cpp 
	$(CXX) -c $(CXXFLAGS) $(INCPATH) -o moc_timinggenerator.o moc_timinggenerator.cpp

####### Install

install:   FORCE

uninstall:   FORCE

FORCE:

