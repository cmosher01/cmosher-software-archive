#
# common.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-01-15
#
# Input variables:
#    MAKEFILE_LIST (built-in make variable; see GNU Make Manual)
#
# Defines some commonly used variables:
#    * VPATH (set to the directory containing the original Makefile)
#      srcdir (Same value as VPATH)
#    * BUILD_TOP (../ directory)
#    * VERS (version number of component being built)
#    * SCM_BASELINE (UNKNOWN_BASELINE)
#      component (the name of the component being built)
#      BUILD_DIR (name of build directory)
#      EMPTY (no value)
#      SPACE (a space character)
#      DOT (a dot, or period, character)
#      COMMA (a comma character)
#
#    * Variables marked with an asterisk above are
#      only defined by this file if they are not
#      already defined. This script will not override
#      any previously-defined value for those variables.
#
# This file also defines the default target as "all".





# the default target for any Makefile is "all"
.DEFAULT_GOAL := all





# echo the current directory path
$(info Building into directory $(realpath .))





# The root of the sources tree (the VPATH)
# If it is not defined yet, then define it as the directory
# containing the (original) Makefile.
ifndef VPATH
VPATH := $(dir $(realpath $(firstword $(strip $(MAKEFILE_LIST)))))
endif

ifneq (,$(findstring :,$(VPATH)))
$(error Multiple directories in VPATH not supported)
endif

srcdir := $(realpath $(VPATH))
ifeq (,$(srcdir))
$(error Cannot find VPATH "$(VPATH)")
endif
$(info srcdir := $(srcdir))




# Define BUILD_TOP as the top level (root) directory
# of the topmake build process; Makefiles will use
# BUILD_TOP to locate files within other (dependent)
# components' build areas.
ifndef BUILD_TOP
BUILD_TOP := $(realpath ..)
endif





# get the name of this component (it will be the name of the
# .depends file at the root of the source tree)
component := $(basename $(notdir $(realpath $(firstword $(wildcard $(srcdir)/*.depends)))))





# Set defaults for version numbers
ifndef VERS
VERS := 1.0
endif
$(info VERS := $(VERS))

ifndef SCM_BASELINE
SCM_BASELINE := UNKNOWN_BASELINE
endif
$(info SCM_BASELINE := $(SCM_BASELINE))





# This defines the build area. It will be a directory
# that gets created in the current default directory
# with the given name.
BUILD_DIR := build

# Make sure directory with same name doesn't exist in
# the source area, because that would cause problems.
ifneq (,$(realpath $(srcdir)/$(BUILD_DIR)))
$(error Directory "$(BUILD_DIR)" in source tree is not allowed: $(realpath $(srcdir)/$(BUILD_DIR)))
endif




# an empty value; can be useful for defining vars with spaces
EMPTY :=
# define space, dot, and comma variables
SPACE := $(EMPTY) $(EMPTY)
DOT   := $(EMPTY).$(EMPTY)
COMMA := $(EMPTY),$(EMPTY)
