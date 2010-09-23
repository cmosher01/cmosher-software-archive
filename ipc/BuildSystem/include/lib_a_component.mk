#
# lib_a_component.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-02-04
#
# Builds a static library (.a file) component.
#








# Get the directory containing this (included) Makefile; we will use
# this variable to find any other include files we need.
IPC_BUILD_INC_DIR := $(dir $(realpath $(lastword $(strip $(MAKEFILE_LIST)))))
include $(IPC_BUILD_INC_DIR)/common.mk
include $(IPC_BUILD_INC_DIR)/tools.mk
include $(IPC_BUILD_INC_DIR)/rules_copy.mk
include $(IPC_BUILD_INC_DIR)/rules_o.mk



CXX_SOURCES := $(shell find $(srcdir) -name \*.cpp -printf "%P\n")

OBJECT_FILES := $(patsubst %.cpp,$(BUILD_DIR)/%.o,$(CXX_SOURCES))

API_INCLUDES := $(patsubst %,$(BUILD_DIR)/%,$(API_INCLUDES))

TARGETS := $(BUILD_DIR)/lib$(LIBNAME).a($(OBJECT_FILES)) $(API_INCLUDES)

# Run this make serially, due to the fact that we are using the ar command.
# See GNU Make Manual, section 11.3 "Dangers When Using Archives."
.NOTPARALLEL:

.PHONY: all

all: $(TARGETS)

CPPFLAGS += -I $(srcdir)

-include $(patsubst %.cpp,$(BUILD_DIR)/%.d,$(CXX_SOURCES))





include $(IPC_BUILD_INC_DIR)/clean.mk

include $(IPC_BUILD_INC_DIR)/directories.mk
TARGET := lib$(LIBNAME).a
ifndef INSTALL_DIR
INSTALL_DIR := $(DESTDIR)/$(libdir)
endif
include $(IPC_BUILD_INC_DIR)/install.mk

include $(IPC_BUILD_INC_DIR)/rpm.mk
