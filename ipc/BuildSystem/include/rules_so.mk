#
# rules_so.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-02-04
#
# Defines a pattern rule for building shared libraries.
#
# Effectively, defines rules for:
# lib%.so.1: %.o
# lib%.so.2: %.o
# ...
# lib%.so.20: %.o
#
# input variables:
#    standard flags for linker:
#        LD, LDFLAGS, TARGET_ARCH, LOADLIBES, LDLIBS


# Define the major version numbers we support.
# For example, in the shared library "libabc.so.1.2.3"
# the major version number is "1".
MAX_MAJOR_VERSION := 20
SO_MAJOR_VERSIONS := $(shell seq $(MAX_MAJOR_VERSION))



# Define patterns for .so files. These will be:
# lib%.so.1 lib%.so.2, and so on.
SO_FILE_PATTERN := $(addprefix lib%.so.,$(SO_MAJOR_VERSIONS))



# Define the pattern rule.
#
# This will be of the form
# lib%.so.1 lib%.so.2 lib%.so.3 ...: %.o
# Which defines how to build, for example,
# libabc.so.2 from abc.o
#
# It uses the filename as the SONAME for the
# library. For example, if it is building
# libabc.so.2, then the SONAME will be
# the same: libabc.so.2
$(SO_FILE_PATTERN): %.o
	$(CXX) $(LDFLAGS) $(TARGET_ARCH) --shared -Wl,--soname=$(@F) $^ $(LOADLIBES) $(LDLIBS) -o $@
