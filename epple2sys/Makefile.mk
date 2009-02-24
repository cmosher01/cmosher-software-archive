# Builds Epple ][ System ROM code

NAME := epple2sys
VERSION := 1.0
ARCH := noarch

DIST = $(NAME)-$(VERSION)

.SUFFIXES:

.SUFFIXES: .s65 .o65 .ld65 .ex65

.s65.o65:
	$(CA65) -v -t apple2 -I $(dir $<) -o $@ $<

.o65.ex65:
	$(LD65) -v -C $(filter %.ld65,$^) -o $@ $(filter %.o65,$^)





.PHONY: all install uninstall clean mostlyclean distclean dist package



epple2sys.ex65: epple2sys.o65 epple2sys.ld65



dist: epple2sys.ld65 epple2sys.s65 hascmap.s65 configure Makefile.mk epple2sys.spec
	rm -fv $(DIST).tar.gz
	rm -Rfv $(DIST)
	mkdir -v $(DIST)
	cp -v $^ $(DIST)
	tar cvzf $(DIST).tar.gz $(DIST)
	rm -Rf $(DIST)



install: all
	mkdir -pv $(DESTDIR)$(PREFIX)/lib/epple2/system/
	cp -v epple2sys.ex65 $(DESTDIR)$(PREFIX)/lib/epple2/system



uninstall:
	rm -fv $(DESTDIR)$(PREFIX)/lib/epple2/system/epple2sys.ex65
	rmdir -pv $(DESTDIR)$(PREFIX)/lib/epple2/system/



clean:
	find . -name "*.ex65" | xargs rm -fv
	find . -name "*.o65" | xargs rm -fv
	rm -fv $(DIST).tar.gz
	rm -Rfv $(DIST)



mostlyclean: clean



distclean: clean
	rm -fv Makefile

RPM := $(abspath rpm)

prefix := $(PREFIX)
ifeq ($(prefix),/usr)
sysconfdir := /etc
else ifeq ($(firstword $(subst /, ,$(prefix))),opt)
sysconfdir := $(abspath /etc/$(prefix))
else
sysconfdir := $(abspath $(prefix)/etc)
endif

package: $(NAME).spec dist
	mkdir -p $(RPM)/{BUILD,BUILDROOT,RPMS,SOURCES,SPECS,SRPMS,VPATH}
	cp $(NAME)-$(VERSION).tar.gz $(RPM)/SOURCES
	touch .rpmmacros
	echo "%_prefix $(prefix)" >>$(RPM)/.rpmmacros
	echo "%_sysconfdir $(sysconfdir)" >>$(RPM)/.rpmmacros
	echo "%_topdir $(RPM)" >>$(RPM)/.rpmmacros
	HOME=$(RPM) rpmbuild -ba --clean --nodeps --buildroot $(RPM)/BUILDROOT $<
	fakeroot alien $(RPM)/RPMS/$(ARCH)/$(NAME)-$(VERSION)-1.$(ARCH).rpm
