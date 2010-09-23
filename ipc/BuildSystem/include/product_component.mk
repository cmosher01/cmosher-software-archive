#
# product_component.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-01-23
#
# Builds the top-level product
#





# Get the directory containing this (included) Makefile; we will use
# this variable to find any other include files we need.
IPC_BUILD_INC_DIR := $(dir $(realpath $(lastword $(strip $(MAKEFILE_LIST)))))
include $(IPC_BUILD_INC_DIR)/common.mk
include $(IPC_BUILD_INC_DIR)/tools.mk
include $(IPC_BUILD_INC_DIR)/rules_copy.mk





# This section builds the list of source files for the project.
ALL_SOURCES := $(shell find $(srcdir) -type f -printf "%P\n")
ALL_SOURCES := $(filter-out Makefile $(component).spec $(component).depends configure, $(ALL_SOURCES))

# targets for building (into build dir)
ALL_TARGETS := $(patsubst %,$(BUILD_DIR)/%,$(ALL_SOURCES))

.PHONY: all

all: $(ALL_TARGETS)



include $(IPC_BUILD_INC_DIR)/clean.mk



include $(IPC_BUILD_INC_DIR)/directories.mk

# For the *product* install, we want
# to create a full skeleton filesystem.
ALL_INSTALL_DIRS := \
	$(DESTDIR)/${bindir} \
	$(DESTDIR)/${sbindir} \
	$(DESTDIR)/${libdir} \
	$(DESTDIR)/${libexecdir} \
	$(DESTDIR)/${includedir} \
	$(DESTDIR)/${damrootdir} \
	$(DESTDIR)/${datarootdir} \
	$(DESTDIR)/${datadir} \
	$(DESTDIR)/${docdir} \
	$(DESTDIR)/${localedir} \
	$(DESTDIR)/${mandir} \
	$(DESTDIR)/${htdocsdir} \
	$(DESTDIR)/${sysconfdir} \
	$(DESTDIR)/${localstatedir} \
	$(DESTDIR)/${sharedstatedir}

.PHONY: install

install: all
	mkdir -p $(ALL_INSTALL_DIRS)



include $(IPC_BUILD_INC_DIR)/rpm.mk

# For the product make, we need the iso target
include $(IPC_BUILD_INC_DIR)/iso.mk
