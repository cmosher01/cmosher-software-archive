SUBDIRS = src conf installer

NAME = epple2
VERSION = 1.0
RPM = /usr/src/rpm
TMP = /tmp/rpm
ARCH = i386

package: $(NAME).spec
	mkdir -p $(TMP)
	cp $(NAME)-$(VERSION).tar.gz $(RPM)/SOURCES
	rpmbuild -ba --clean --buildroot $(TMP) $<
	fakeroot alien $(RPM)/RPMS/$(ARCH)/$(NAME)-$(VERSION)-1.$(ARCH).rpm
