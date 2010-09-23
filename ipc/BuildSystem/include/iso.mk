#
# iso.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-02-24
#
# Defines the standard "iso" target.
#
#
#
# Input variables:
#     component (name of the product component)
#     VERS (version of the product)
#     MKISOFSFLAGS (optional; additional flags for mkisofs command)


PKG_FILES := $(shell find $(BUILD_TOP) -name \*.rpm)

MKISOFSFLAGS ?= -r -J

.PHONY: iso

iso: $(BUILD_TOP)/$(component)-$(VERS).iso

$(BUILD_TOP)/$(component)-$(VERS).iso: $(PKG_FILES)
	mkisofs -o $@ -V $(basename $(@F)) $(MKISOFSFLAGS) $^
