#
# Makefile for app1cpp
# Copyright (C) 2009, by IPC, Fairfield, CT
#
# author: Chris Mosher
# creation date: 2009-01-24
#

# define the installation prefix
prefix := /opt/ipc/onems

# define the target binary executable file
TARGET = $(component)



# This component uses the static library component abc.
# Define the build area for the abc component (this
# variable is only for our use, below, in this Makefile).
ABC_DIR = $(BUILD_TOP)/abc/build
# Tell the compiler where to find include files for abc
# by using the "-I" option:
CPPFLAGS += -I $(ABC_DIR)
# Tell the linker to reference the abc.a library ("-l"), and
# where to find it ("-L"):
LDLIBS += -L $(ABC_DIR) -labc




VOBROOT := /usr/vobs

include $(VOBROOT)/ipc_build/include/cpp_component.mk
