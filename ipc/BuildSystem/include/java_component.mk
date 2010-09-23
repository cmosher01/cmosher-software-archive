#
# java_component.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-01-09
#
# Builds a Java component.
#





# Get the directory containing this (included) Makefile; we will use
# this variable to find any other include files we need.
IPC_BUILD_INC_DIR := $(dir $(realpath $(lastword $(strip $(MAKEFILE_LIST)))))
include $(IPC_BUILD_INC_DIR)/common.mk
include $(IPC_BUILD_INC_DIR)/tools.mk
include $(IPC_BUILD_INC_DIR)/rules_copy.mk





# extract the simple version number for the
# java compiler (for example, convert JAVA_VERSION
# "1.5.0.16" to JAVA_SIMPLE_VERS "1.5")
JAVA_SIMPLE_VERS := $(subst $(DOT),$(SPACE),$(JAVA_VERSION))
JAVA_SIMPLE_VERS := $(wordlist 1,2,$(JAVA_SIMPLE_VERS))
JAVA_SIMPLE_VERS := $(subst $(SPACE),$(DOT),$(JAVA_SIMPLE_VERS))

ifdef JAVAC_CLASSPATH
BUILD_CLASSPATH_FLAG := -Djavac.classpath=$(JAVAC_CLASSPATH)
else
BUILD_CLASSPATH_FLAG :=
endif

ANT_DEFS := \
	-Dsrcdir=$(srcdir) \
	-Dcomponent=$(component) \
	-Dbuild.top=$(BUILD_TOP) \
	$(BUILD_CLASSPATH_FLAG) \
	-Djavac=$(JAVAC) \
	-Djavac.version=$(JAVA_SIMPLE_VERS)





.PHONY: all

all: $(BUILD_DIR)/build.xml
	[[ -d $(BUILD_DIR) ]] || mkdir -p $(BUILD_DIR) && :
	cd $(BUILD_DIR) && $(ANT) $(ANTFLAGS) $(ANT_DEFS)





.PHONY: check

check: all
	cd $(BUILD_DIR)/test && $(JUNIT) --verbose $(MAIN_TEST)





include $(IPC_BUILD_INC_DIR)/clean.mk

include $(IPC_BUILD_INC_DIR)/directories.mk
ifndef INSTALL_DIR
INSTALL_DIR := $(DESTDIR)/$(libdir)/$(component)
endif
include $(IPC_BUILD_INC_DIR)/install.mk

include $(IPC_BUILD_INC_DIR)/rpm.mk
