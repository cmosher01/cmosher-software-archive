#
# lib_so_component.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-02-04
#
# Builds a shared library (.so file) component.
#








# Get the directory containing this (included) Makefile; we will use
# this variable to find any other include files we need.
IPC_BUILD_INC_DIR := $(dir $(realpath $(lastword $(strip $(MAKEFILE_LIST)))))
include $(IPC_BUILD_INC_DIR)/common.mk
include $(IPC_BUILD_INC_DIR)/tools.mk
include $(IPC_BUILD_INC_DIR)/rules_copy.mk
include $(IPC_BUILD_INC_DIR)/rules_o.mk
include $(IPC_BUILD_INC_DIR)/rules_so.mk


API_INCLUDES := $(patsubst %,$(BUILD_DIR)/%,$(API_INCLUDES))

TARGETS := $(BUILD_DIR)/lib$(LIBNAME).so.$(LIBMAJOR).$(LIBMINOR) $(API_INCLUDES)

.PHONY: all

all: $(TARGETS)



CXX_SOURCES := $(shell find $(srcdir) -name \*.cpp -printf "%P\n")

CPPFLAGS += -I $(srcdir)

CXXFLAGS += -fPIC



stripvers = $(shell echo '$1' | sed 's/\.so\..*/.so/')

$(BUILD_DIR)/lib$(LIBNAME).so.$(LIBMAJOR).$(LIBMINOR): $(BUILD_DIR)/lib$(LIBNAME).so.$(LIBMAJOR)
	mv $< $@
	ln -f -s $(@F) $<
	ln -f -s $(<F) $(call stripvers,$<)

$(BUILD_DIR)/lib$(LIBNAME).so.$(LIBMAJOR): $(patsubst %.cpp,$(BUILD_DIR)/%.o,$(CXX_SOURCES))

-include $(patsubst %.cpp,$(BUILD_DIR)/%.d,$(CXX_SOURCES))





include $(IPC_BUILD_INC_DIR)/clean.mk

include $(IPC_BUILD_INC_DIR)/directories.mk
ifndef INSTALL_DIR
INSTALL_DIR := $(DESTDIR)/$(libdir)
endif
include $(IPC_BUILD_INC_DIR)/install_so.mk

include $(IPC_BUILD_INC_DIR)/rpm.mk
