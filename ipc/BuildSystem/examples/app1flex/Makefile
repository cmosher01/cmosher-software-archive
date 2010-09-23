#
# Makefile for app1flex
# Copyright (C) 2009, by IPC, Fairfield, CT
#
# author: Chris Mosher
# creation date: 2009-01-16
#


# define the installation prefix
prefix := /opt/ipc/onems

# define the target swf file
TARGET = $(component).swf

# define the main app mxml src file
MAIN_APP = $(component).mxml

# define this extra class to include in
# the final swf file (this class is not
# referenced by the main app, so it wouldn't
# be pulled in automatically by the
# compiler unless we specify it here).
EXTRA_CLASSES = other.MyThing

# We depend on classes in the lib1flex component,
# so we need to reference lib1flex.swc here (in
# it's final target location).
MXMLC_LIBRARIES = $(BUILD_TOP)/lib1flex/build/lib1flex.swc

# define the versions of tools to use
JAVA_VERSION := 1.5.0.16
FLEX_VERSION := 3.1.0.2710




VOBROOT := /usr/vobs

include $(VOBROOT)/ipc_build/include/flex_component.mk
