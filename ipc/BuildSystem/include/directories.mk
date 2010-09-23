#
# directories.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2008-12-22
#
# Sets up variables for installation directories.
#
#
#
# The installation directories are based on two sources:
#
# 1) GNU Coding Standards (http://www.gnu.org/prep/standards/),
#    section 7.2.5, "Variables for Installation Directories"
#
# 2) Filesystem Hierarchy Standard (http://www.pathname.com/fhs/)
#
#
#
# INPUT VARIABLE:
#
#    prefix   Must be defined as the root of the hierarchy.
#             There is no default value; the variable must
#             be specified.
#             Typical values would be:
#                 /
#                 /usr
#                 /usr/local
#                 /opt/ipc/
#                 /opt/ipc/PRODUCT (for some PRODUCT)
#



# First, ensure that prefix is set to something.
# We do not default it to "/" or "/usr/local" or anything.
# This is for safety's sake; the user must specify it.
ifndef prefix
$(error Must define prefix variable)
endif

# We just build up the paths as defined in the standards.
# We use abspath around everything just to remove any double slashes.

exec_prefix   := $(abspath $(prefix))

bindir        := $(abspath $(exec_prefix)/bin)
sbindir       := $(abspath $(exec_prefix)/sbin)
libdir        := $(abspath $(exec_prefix)/lib)
libexecdir    := $(abspath $(exec_prefix)/libexec)
includedir    := $(abspath $(prefix)/include)
oldincludedir := $(abspath /usr/include)



datarootdir   := $(abspath $(prefix)/share)
datadir       := $(abspath $(datarootdir))
docdir        := $(abspath $(datarootdir)/doc)
localedir     := $(abspath $(datarootdir)/locale)
mandir        := $(abspath $(datarootdir)/man)
htdocsdir     := $(abspath $(datarootdir)/htdocs)
webappsdir    := $(abspath $(datarootdir)/webapps)
damrootdir    := $(abspath $(prefix)/dam)



# According to FHS, we need to handle
# /var and /etc differently for different prefixes.
#
# For example: for a prefix of /usr/local, we would use:
# /usr/local/etc
# but for a prefix of /opt/ipc/onems, would use:
# /etc/opt/ipc/onems
#
# For /usr, we need to use /etc and /var (not
# /usr/etc nor /usr/var)
#
# For /usr/local, we need to use /var/local



# Pull out the top-most directory name
# To do this, we convert slashes to spaces in the directory path,
# then pull out the first word.
prefix_top_dir := $(firstword $(strip $(subst /,$(SPACE),$(prefix))))



ifeq ($(prefix_top_dir),opt)# /opt/... hierarchy

sysconfdir    := $(abspath /etc/$(prefix))
localstatedir := $(abspath /var/$(prefix))

else ifeq ($(prefix),/usr) # /usr hierarchy

sysconfdir    := $(abspath /etc)
localstatedir := $(abspath /var)

else ifeq ($(prefix),/usr/local) # /usr/local hierarchy

sysconfdir    := $(abspath /usr/local/etc)
localstatedir := $(abspath /var/local)

else # / hierarchy or some other (non-standard) hierarchy

sysconfdir    := $(abspath $(prefix)/etc)
localstatedir := $(abspath $(prefix)/var)

endif



sharedstatedir := $(abspath $(localstatedir))
