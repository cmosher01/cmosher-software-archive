#
# Makefile for abc
# Copyright (C) 2009, by IPC, Fairfield, CT
#
# author: Chris Mosher
# creation date: 2009-02-05
#
# Example Makefile for static library (.a file)

# define the installation prefix
prefix := /opt/ipc/onems

# Define the name of the library.
# We are building libabc.a, so the library
# name is just $(component), which is "abc"
LIBNAME = $(component)

# We only have one include file for our API.
# As is often the case, this is just the component name ".h"
API_INCLUDES = $(component).h


VOBROOT := /usr/vobs

include $(VOBROOT)/ipc_build/include/lib_a_component.mk
