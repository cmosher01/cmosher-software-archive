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

# Define the major and minor version numbers for
# the library. The major version number will change
# only when we make a non-backward-compatible change.
# (Users will need to re-link to use the new version
# if we change the major version number.)
# For example, for version 1.0, use major 1 and minor 0
#LIBMAJOR := 1
#LIBMINOR := 0
# The minor version number can actually contain more
# than just one number. For example, to represent
# version number 2.1.3a, use 2 for the major version
# and 1.3a for the minor version
#LIBMAJOR := 2
#LIBMINOR := 1.3a
LIBMAJOR := 1
LIBMINOR := 0

# Define the name of the library.
# This will normally be just the name of the component
#LIBNAME = $(component)
# For example, for component "mine", the library will
# be "libmine.so", and the library name will be "mine".
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

include $(VOBROOT)/ipc_build/include/lib_so_component.mk
