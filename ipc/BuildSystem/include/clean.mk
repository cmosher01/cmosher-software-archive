#
# tools.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-01-16
#
# Defines the standard "clean" target.
#
# input variables:
#    BUILD_DIR (the name of the "build" directory)





.PHONY: clean

# clean:
# just remove the entire build tree
clean:
	-rm -Rf $(BUILD_DIR)
