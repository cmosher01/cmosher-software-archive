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

# Define version of tools to use
PHPUNIT_VERSION := 3.3.9

# define our phpunit test-suite class
MAIN_TEST := AllTests


VOBROOT := /usr/vobs

include $(VOBROOT)/ipc_build/include/php_component.mk
