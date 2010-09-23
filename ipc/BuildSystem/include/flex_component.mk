#
# flex_component.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-01-09
#
# Builds a FLEX component.
#





# Get the directory containing this (included) Makefile; we will use
# this variable to find any other include files we need.
IPC_BUILD_INC_DIR := $(dir $(realpath $(lastword $(strip $(MAKEFILE_LIST)))))

include $(IPC_BUILD_INC_DIR)/common.mk
include $(IPC_BUILD_INC_DIR)/tools.mk
include $(IPC_BUILD_INC_DIR)/rules_copy.mk
include $(IPC_BUILD_INC_DIR)/rules_mxmlc.mk





.PHONY: all

all: $(BUILD_DIR)/$(TARGET)

ALL_SOURCES := $(shell find $(srcdir) -type f -printf "%P\n")

$(BUILD_DIR)/$(TARGET) : $(MAIN_APP) $(ALL_SOURCES)





include $(IPC_BUILD_INC_DIR)/clean.mk

include $(IPC_BUILD_INC_DIR)/directories.mk
ifndef INSTALL_DIR
INSTALL_DIR := $(DESTDIR)/$(libdir)/$(component)
endif
include $(IPC_BUILD_INC_DIR)/install.mk

include $(IPC_BUILD_INC_DIR)/rpm.mk
