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

# Define the target. For FLEX components that are libraries,
# this will normally be $(component).swc
#TARGET = $(component).swc
#
TARGET = $(component).swc

# Define the main application as or mxml file. This will
# normally be $(component).as or $(component).mxml
#MAIN_APP = $(component).as
#
MAIN_APP = $(component).as

# The MXMLC compiler will automatically include any classes
# referenced by the main application (as define above). If
# there are any other classes beyond those that need to be
# included as well, you can define them here. For example:
#EXTRA_CLASSES := com.ipc.onems.Something com.ipc.onems.Example
#

# Define any libraries (swc files) that this component
# depends on. For example:
#MXMLC_LIBRARIES := $(BUILD_TOP)/someflexlib/build/someflexlib.swc
#

# define the versions of tools to use
JAVA_VERSION := 1.5.0.16
FLEX_VERSION := 3.1.0.2710


VOBROOT := /usr/vobs

include $(VOBROOT)/ipc_build/include/flex_component.mk
