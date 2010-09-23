#
# Makefile for lib1flex
# Copyright (C) 2009, by IPC, Fairfield, CT
#
# author: Chris Mosher
# creation date: 2009-01-16
#
# Example Makefile for FLEX-based component

# define the installation prefix
prefix := /opt/ipc/onems

# define the target swc file
TARGET = $(component).swc

# define the main application class file
MAIN_APP = $(component).as

# define two extra classes (not referenced from the main class)
EXTRA_CLASSES := lib1.BooleanComboBox lib1.Something

# Define versions of tools to use
JAVA_VERSION := 1.5.0.16
FLEX_VERSION := 3.1.0.2710


VOBROOT := /usr/vobs

include $(VOBROOT)/ipc_build/include/flex_component.mk
