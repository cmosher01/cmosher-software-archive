#
# install.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-01-16
#
# Defines the standard install target.
# Copies the target(s) of the build into
# their final run-time directories (like /bin or /lib)
#
# input variables:
#    TARGET (the binary to install; or * to install everything in BUILD_DIR, recursively)
#    INSTALL_DIR (the final installation directory; must account for DESTDIR and prefix)
#    BUILD_DIR (the build directory, containing the binary targets to be installed)
#
# The "install" target has the "all" target as a prerequisite, so the "all" target must
# be defined by the including Makefile.
#



.PHONY: install

install: all
	[[ -d $(INSTALL_DIR) ]] || mkdir -p $(INSTALL_DIR) && :
	cp -Rf $(INSTALL_CP_FLAGS) $(BUILD_DIR)/$(TARGET) $(INSTALL_DIR)
