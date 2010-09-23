#
# bin_component.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-01-24
#
# Builds a component that doesn't have any source code,
# just binaries to be installed.



# Get the directory containing this (included) Makefile; we will use
# this variable to find any other include files we need.
IPC_BUILD_INC_DIR := $(dir $(realpath $(lastword $(strip $(MAKEFILE_LIST)))))
include $(IPC_BUILD_INC_DIR)/common.mk
include $(IPC_BUILD_INC_DIR)/rules_copy.mk

.PHONY: all

all: $(patsubst %,$(BUILD_DIR)/%,$(ALL_BINS))

include $(IPC_BUILD_INC_DIR)/clean.mk
include $(IPC_BUILD_INC_DIR)/directories.mk
ifndef TARGET
TARGET := *
endif
include $(IPC_BUILD_INC_DIR)/install.mk
include $(IPC_BUILD_INC_DIR)/rpm.mk
