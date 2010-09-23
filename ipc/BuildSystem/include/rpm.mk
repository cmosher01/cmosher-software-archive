#
# rpm.mk
# Copyright (C) 2009, by IPC, Fairfield, CT.
#
# author: Chris Mosher
# creation date: 2009-01-15
#
# Defines the standard "rpm" target.
#
#
#
# Input variables:
#    install directory variables:
#        prefix,exec_prefix,bindir,sbindir,libdir,libexecdir,includedir,
#        oldincludedir,sysconfdir,localstatedir,sharedstatedir,datarootdir,
#        datadir,docdir,localedir,mandir,htdocsdir
#    srcdir (the absolute path of the root of the source tree being built)
#    component (the name of the component being built)
#    VERS (the version number of the component)
#    SCM_BASELINE (baseline tag from SCM; used in rpm description)









.PHONY: package rpm

package: rpm

BUILDAREA := $(abspath .)
RPMDIR := $(abspath rpm)
RPMBUILD := rpmbuild -bb $(RPMBUILDFLAGS) --buildroot=$(RPMDIR)/BUILDROOT
IPCMAKEINSTALL := $(MAKE) -C $(BUILDAREA) -f $(srcdir)/Makefile install DESTDIR=%{buildroot}

define build_rpmmacros
echo "%_prefix          $(prefix)         "  >rpm/.rpmmacros
echo "%_exec_prefix     $(exec_prefix)    " >>rpm/.rpmmacros
echo "%_bindir          $(bindir)         " >>rpm/.rpmmacros
echo "%_sbindir         $(sbindir)        " >>rpm/.rpmmacros
echo "%_libdir          $(libdir)         " >>rpm/.rpmmacros
echo "%_libexecdir      $(libexecdir)     " >>rpm/.rpmmacros
echo "%_includedir      $(includedir)     " >>rpm/.rpmmacros
echo "%_oldincludedir   $(oldincludedir)  " >>rpm/.rpmmacros
echo "%_sysconfdir      $(sysconfdir)     " >>rpm/.rpmmacros
echo "%_localstatedir   $(localstatedir)  " >>rpm/.rpmmacros
echo "%_sharedstatedir  $(sharedstatedir) " >>rpm/.rpmmacros
echo "%_damrootdir      $(damrootdir)     " >>rpm/.rpmmacros
echo "%_datarootdir     $(datarootdir)    " >>rpm/.rpmmacros
echo "%_datadir         $(datadir)        " >>rpm/.rpmmacros
echo "%_docdir          $(docdir)         " >>rpm/.rpmmacros
echo "%_defaultdocdir   $(docdir)         " >>rpm/.rpmmacros
echo "%_localedir       $(localedir)      " >>rpm/.rpmmacros
echo "%_mandir          $(mandir)         " >>rpm/.rpmmacros
echo "%_htdocsdir       $(htdocsdir)      " >>rpm/.rpmmacros
echo "%_webappsdir      $(webappsdir)     " >>rpm/.rpmmacros
echo "%debug_package    %{nil}            " >>rpm/.rpmmacros
echo "%_topdir          $(RPMDIR)         " >>rpm/.rpmmacros
echo "%ipc_make_install $(IPCMAKEINSTALL) " >>rpm/.rpmmacros
echo "%buildarea        $(BUILDAREA)      " >>rpm/.rpmmacros
echo "%component        $(component)      " >>rpm/.rpmmacros
echo "%VERS             $(VERS)           " >>rpm/.rpmmacros
echo "%SCM_BASELINE     $(SCM_BASELINE)   " >>rpm/.rpmmacros
endef

rpm: $(component).spec
	mkdir -p rpm/{BUILD,BUILDROOT,RPMS,SOURCES,SPECS,SRPMS}
	@$(build_rpmmacros)
	HOME=$(RPMDIR) BUILD_TOP=$(realpath ..) $(RPMBUILD) $<
