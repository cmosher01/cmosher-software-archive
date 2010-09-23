#
# Makefile for xyz
# Copyright (C) 2009, by IPC, Fairfield, CT
#
# author: Chris Mosher
# creation date: 2009-02-05
#
# Example Makefile for shared library (.so file)

# define the installation prefix
prefix := /opt/ipc/onems

# Define the major and minor version numbers for
# our library. The major version number will change
# only when we make a non-backward-compatible change.
# (Users will need to re-link to use the new version
# if we change the major version number.)
LIBMAJOR := 2
LIBMINOR := 3.4

# Define the name of our library. We are building
# libxyz.so.2.3.4 therefore our library name is
# just $(component) which will be "xyz".
LIBNAME = $(component)

# We expose xyz.h for our users to include
API_INCLUDES = $(component).h


VOBROOT := /usr/vobs

include $(VOBROOT)/ipc_build/include/lib_so_component.mk
