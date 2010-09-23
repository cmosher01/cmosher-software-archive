#
# Makefile for ???COMPONENT???
# Copyright (C) ???YEAR???, by ???IPC, Fairfield, CT???
#
# author: ???NAME???
# creation date: ???YYYY-MM-DD???
#

# Define the installation prefix
# This will ususally be /opt/ipc/product,
# where product is the name of the product,
# for example:
#prefix := /opt/ipc/onems
# but for 3rd party software it should be
# /usr/local
#prefix := /usr/local
#
# Never include any subdirectories like
# bin, lib, or etc in the prefix definition
#
prefix := /opt/ipc/onems

# Define the name of the library.
# This will normally be just the name of the component
#LIBNAME = $(component)
# For example, for component "mine", the library will
# be "libmine.a", and the library name will be "mine".
LIBNAME = $(component)

# List the header file (or files) that define our API.
# Only list those header files that we want to expose
# to the users of our library; don't just list every
# .h file that exists. For many libraries, there will
# be just one header file, named comp.h (where comp is
# the name of the component). For such a case, we can
# just use:
#API_INCLUDES = $(component).h
# Mulitple header files would be delimited with spaces:
#API_INCLUDES = comp1.h comp2.h comp3.h
API_INCLUDES = $(component).h


VOBROOT := /usr/vobs

include $(VOBROOT)/ipc_build/include/lib_a_component.mk
