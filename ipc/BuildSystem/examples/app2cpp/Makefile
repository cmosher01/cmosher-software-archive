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



# This component uses the shared library component xyz.
# Define the build area for the xyz component (this
# variable is only for our use, below, in this Makefile).
XYZ_DIR = $(BUILD_TOP)/xyz/build
# Tell the compiler where to find include files for xyz
# by using the "-I" option:
CPPFLAGS += -I $(XYZ_DIR)
# Tell the linker to reference the libxyz.so library ("-l"), and
# where to find it ("-L").
LDLIBS += -L $(XYZ_DIR) -lxyz




VOBROOT := /usr/vobs

include $(VOBROOT)/ipc_build/include/cpp_component.mk
