#
# Makefile for cmdqued
# Copyright (C) 2009, by IPC, Fairfield, CT
#
# author: Chris Mosher
# creation date: 2009-01-24
#

# define the installation prefix
prefix := /opt/ipc/onems

# define the target binary executable file
TARGET = $(component)

# Override the default installation directory
# (default is bin, we want sbin because our
# executable runs as a *daemon*).
# Always remember to use DESTDIR to support RPM builds.
INSTALL_DIR = $(DESTDIR)/$(sbindir)


VOBROOT := /usr/vobs

include $(VOBROOT)/ipc_build/include/cpp_component.mk
