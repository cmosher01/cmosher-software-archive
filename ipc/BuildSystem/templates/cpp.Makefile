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

# define the target binary executable file
TARGET = $(component)




# If you need to include/link another (IPC) shared library
# component, you'll need to add that component to this
# component's .spec file and .depends file, and you'll need
# to set flags for the compiler and linker to find that
# component when building. You will typically be adding
# the "-I" flag to tell the compiler where to find the
# header files of the library; and the "-L" and "-l" flags
# to tell the linker where to find the library itself (.so or .a).
# When locating these files (in other dependent components) at
# buildtime, keep in mind that topmake (the Product Build
# Process) will place them in $(BUILD_TOP)/component/build
# (where component is the name of the component). So, for
# example, you woulde reference a component named xyz as:
XYZ_DIR = $(BUILD_TOP)/xyz/build
# To tell the compiler where to find include files for xyz
# use the "-I" option as follows:
CPPFLAGS += -I $(XYZ_DIR)
# To tell the linker to reference the libxyz.so library
# by using the "-l" (lowercase ell) option; and where to find it, by
# using the "-L" (uppercase ell) option. Note that the -L option
# requires a space after the L, and the -l option requieres NO space
# after the l, as shown:
LDLIBS += -L $(XYZ_DIR) -lxyz
# The above two options will tell the linker to link against libxyz.so
# or libxyz.a in $(XYZ_DIR). Note that libxyz.so will typically be
# a symlink to the so-name file, which will in turn be a symlink to
# the actual library file. For example:
# xyz/build/libxyz.so -> libxyz.so.1         (ld-name symlink)
# xyz/build/libxyz.so.1 -> libxyz.so.1.2.3   (so-name symlink)
# xyz/build/libxyz.so.1.2.3                  (actual lib file)





# The default installation directory for cpp
# applications is the bin directory.
#
# You will need to override the default installation
# directory for daemons, to sbin:
#INSTALL_DIR = $(DESTDIR)/$(sbindir)
# Always remember to use DESTDIR to support RPM builds.


VOBROOT := /usr/vobs

include $(VOBROOT)/ipc_build/include/cpp_component.mk
