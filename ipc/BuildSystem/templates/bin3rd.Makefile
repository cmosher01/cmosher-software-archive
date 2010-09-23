#
# Makefile for binary (3rd party component)
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-02-08
#
# Installs a 3rd party component that's provided
# as binaries (without a suitable RPM; if it has
# a suitable RPM, just use that instead).



# Define the installation prefix. For 3rd party
# components, the policy is to use /usr/local
prefix := /usr/local

# Define ALL_BINS to be the list of all binary files
# to be installed. The following example just
# finds all provided files.
ALL_BINS = $(shell find $(srcdir) -printf "%P\n")

# Define the installation directory for the binaries.
# You must use $(DESTDIR) to allow for RPM builds.
# For example:
#INSTALL_DIR = $(DESTDIR)/$(bindir)
#INSTALL_DIR = $(DESTDIR)/$(libdir)/$(component)
INSTALL_DIR = $(DESTDIR)/$(bindir)


VOBROOT := /usr/vobs

include $(VOBROOT)/ipc_build/include/bin_component.mk
