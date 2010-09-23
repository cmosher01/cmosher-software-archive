#
# script_component.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-01-09
#
# Builds a component consisting of scripts.
#***** all scripts must be in root directory of srcdir!!! ???should we change this???
#





# Get the directory containing this (included) Makefile; we will use
# this variable to find any other include files we need.
IPC_BUILD_INC_DIR := $(dir $(realpath $(lastword $(strip $(MAKEFILE_LIST)))))
include $(IPC_BUILD_INC_DIR)/common.mk
include $(IPC_BUILD_INC_DIR)/tools.mk
include $(IPC_BUILD_INC_DIR)/rules_copy.mk





# This section builds the list of source files for the project.
ALL_SOURCES := $(shell find $(srcdir) -maxdepth 1 -type f -printf "%P\n")
ALL_SOURCES := $(filter-out Makefile $(component).spec $(component).depends configure, $(ALL_SOURCES))

# targets for building (into build dir)
ALL_TARGETS := $(patsubst %,$(BUILD_DIR)/%,$(ALL_SOURCES))

.PHONY: all

all: $(ALL_TARGETS)



include $(IPC_BUILD_INC_DIR)/clean.mk

include $(IPC_BUILD_INC_DIR)/directories.mk
ifndef INSTALL_DIR
INSTALL_DIR := $(DESTDIR)/$(bindir)
endif
TARGET := *
include $(IPC_BUILD_INC_DIR)/install.mk

include $(IPC_BUILD_INC_DIR)/rpm.mk
