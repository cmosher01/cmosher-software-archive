SUBDIRS = src conf installer

NAME = epple2
VERSION = 1.0
ARCH = i386

EXTRA_DIST = COPYING.sdl COPYING.other epple2.spec

RPM := $(abspath rpm)

package: $(NAME).spec dist
	mkdir -p $(RPM)/{BUILD,BUILDROOT,RPMS,SOURCES,SPECS,SRPMS,VPATH}
	cp $(NAME)-$(VERSION).tar.gz $(RPM)/SOURCES
	touch .rpmmacros
	echo "%_prefix $(prefix)" >>$(RPM)/.rpmmacros
	echo "%_sysconfdir $(sysconfdir)" >>$(RPM)/.rpmmacros
	echo "%_topdir $(RPM)" >>$(RPM)/.rpmmacros
	HOME=$(RPM) rpmbuild -ba --clean --buildroot $(RPM)/BUILDROOT $<
	fakeroot alien $(RPM)/RPMS/$(ARCH)/$(NAME)-$(VERSION)-1.$(ARCH).rpm
