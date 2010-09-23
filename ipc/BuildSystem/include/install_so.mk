#
# install_so.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-02-04
#
# Defines the standard install target for shared libraries (.so files).
#
# input variables:
#    LIBNAME (name of library, for example, abc for building libabc.so.1.2.3)
#    LIBMAJOR (major version, for example, 1 for building libabc.so.1.2.3)
#    LIBMINOR (minor version, for example, 2.3 for building libabc.so.1.2.3)
#    BUILD_DIR (the build directory, containing the .so file to be installed)
#    INSTALL_DIR (installation directory; will usually be $(DESTDIR)/$(libdir) )
#
# The "install" target has the "all" target as a prerequisite, so the "all" target must
# be defined by the including Makefile.
#



LIB_SO_NAME = lib$(LIBNAME).so.$(LIBMAJOR)
LIB_FL_NAME = lib$(LIBNAME).so.$(LIBMAJOR).$(LIBMINOR)

.PHONY: install

install: all
	[[ -d $(INSTALL_DIR) ]] || mkdir -p $(INSTALL_DIR) && :
	cp -f $(BUILD_DIR)/$(LIB_FL_NAME) $(INSTALL_DIR)
	ln -f -s $(LIB_FL_NAME) $(INSTALL_DIR)/$(LIB_SO_NAME)
