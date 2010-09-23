#
# Product Build Makefile
# Copyright (C) 2009, by IPC, Fairfield, CT.
# Builds an entire product.
#
# author: Chris Mosher
# creation date: 2009-02-25
#
# Creates a new build area directory, if necessary.
# Calls topmake to produce the top-level makefile.
# Runs the product build using that makefile (which builds
# each component and creates its RPM package).
# Builds the "iso" target of the product.
#
# input variables:
#     PRODUCT       requried; name of product to build; the product
#                   directory must exist in /usr/vobs/ipc_product)
#
# Of course, any variable in the script may be overridden on
# the command line as desired. For example:
#
#     TOPBUILD_DIR  override this to define a build area to
#                   re-use instead of creating a fresh directory,
#                   which defaults to /usr/vobs/ipc_product/build/$(TOPBUILD_NAME)
#
#     TOPBUILD_NAME name of the new build directory to create; defaults
#                   to `date +%s`.`whoami`; only used in the definition
#                   of TOPBUILD_DIR, so if you override TOPBUILD_DIR, you
#                   do not need to override TOPBUILD_NAME.
#
#     PRODUCT_DIR   override this to define where the product
#                   directory is, instead of /usr/vobs/ipc_product
#
#     CC_ATTR_FILE  file from which to get ClearCase attributes VERS
#                   and SCM_BASELINE; defaults to the product Makefile
#
#     TOPMAKE_SOURCE_ROOTS
#                   list of root directories in which topmake will
#                   search for components (*.depends files); defaults
#                   to /usr/vobs
#



ifndef PRODUCT
$(error Must define PRODUCT variable as the name of the product to build)
endif



# Root directory where source ClearCase VOBs are mounted
VOBS := /usr/vobs

# Root directory within which to search for sources.
TOPMAKE_SOURCE_ROOTS = $(VOBS)

# topmake script (which produces the makefile to build all components)
TOPMAKE = $(VOBS)/ipc_build/include/topmake

# Product directory, which contains one directory for each
# "product-level" component.
# There is not a hierarchical structure within this directory;
# each product directory is supposed to be directly in the
# PRODUCT_DIR directory.
PRODUCT_DIR = $(VOBS)/ipc_product

# The Makefile for the product we are building
PRODUCT_MAKEFILE = $(PRODUCT_DIR)/$(PRODUCT)/Makefile

# Target for topmake to build for each component. Export it so
# topmake's output makefile can use it.
COMPONENT_TARGET = package
export COMPONENT_TARGET

# Target to build for the product (after all component are built)
PRODUCT_TARGET = iso

# Define the directory (top-level) for the entire build output.
# First set the root where all these directories go:
TOPBUILD_DIR_AREA := $(PRODUCT_DIR)/build
# Then create the name of the build area directory. We want this to
# be unique, so we use seconds since epoch which
# should be unique in 99.999% of the cases, then we append the username
# of the user doing the build to make extra sure we're unique.
# NOTE: We could have used uuidgen instead, but the name might be unwieldy.
TOPBUILD_NAME := $(shell date +%s).$(shell whoami)
TOPBUILD_DIR := $(TOPBUILD_DIR_AREA)/$(TOPBUILD_NAME)



# Shell command to check if ClearCase is available in the environment
CC_CHECK := which cleartool >/dev/null 2>&1

# Shell command to retrieve an attribute from a ClearCase file
# usage: $(call CC_ATTR,attribute)
# where attribute is the name of the attribute to retrieve
CC_ATTR = $(CC_CHECK) && cleartool describe -fmt "%[$(1)]NSa"

# Pull VERS and SCM_BASELINE values from ClearCase attributes on the
# product Makefile, if available. Export them so the components
# will use them for their builds.
CC_ATTR_FILE = $(PRODUCT_MAKEFILE)
VERS = $(subst ",,$(shell $(call CC_ATTR,VERS) $(CC_ATTR_FILE)))
SCM_BASELINE = $(subst ",,$(shell $(call CC_ATTR,SCM_BASELINE) $(CC_ATTR_FILE)))
export VERS SCM_BASELINE



.PHONY: $(PRODUCT)

$(PRODUCT):
	@date --utc --rfc-3339=seconds
	mkdir -p $(TOPBUILD_DIR)
	cd $(TOPBUILD_DIR) && $(TOPMAKE) $(TOPMAKE_SOURCE_ROOTS) | $(MAKE) -f - $(PRODUCT)
	@date --utc --rfc-3339=seconds
	cd $(TOPBUILD_DIR) && $(MAKE) -C $(PRODUCT) -f $(PRODUCT_MAKEFILE) $(PRODUCT_TARGET)
	@date --utc --rfc-3339=seconds
