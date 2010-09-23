#
# cpp_component.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-01-23
#
# Builds a C++ component.
#








# Get the directory containing this (included) Makefile; we will use
# this variable to find any other include files we need.
IPC_BUILD_INC_DIR := $(dir $(realpath $(lastword $(strip $(MAKEFILE_LIST)))))
include $(IPC_BUILD_INC_DIR)/common.mk
include $(IPC_BUILD_INC_DIR)/tools.mk
include $(IPC_BUILD_INC_DIR)/rules_copy.mk
include $(IPC_BUILD_INC_DIR)/rules_o.mk



.PHONY: all

all: $(BUILD_DIR)/$(TARGET)

CXX_SOURCES := $(shell find $(srcdir) -name \*.cpp -printf "%P\n")

CPPFLAGS += -I $(srcdir)

$(BUILD_DIR)/$(TARGET): $(patsubst %.cpp,$(BUILD_DIR)/%.o,$(CXX_SOURCES))
	$(CXX) $(LDFLAGS) $(TARGET_ARCH) $^ $(LOADLIBES) $(LDLIBS) -o $@

-include $(patsubst %.cpp,$(BUILD_DIR)/%.d,$(CXX_SOURCES))





include $(IPC_BUILD_INC_DIR)/clean.mk

include $(IPC_BUILD_INC_DIR)/directories.mk
ifndef INSTALL_DIR
INSTALL_DIR := $(DESTDIR)/$(bindir)
endif
include $(IPC_BUILD_INC_DIR)/install.mk

include $(IPC_BUILD_INC_DIR)/rpm.mk
