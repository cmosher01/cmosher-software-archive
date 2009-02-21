# Builds Epple ][ System ROM code

NAME := epple2sys
VERSION := 1.0

DIST = $(NAME)-$(VERSION)

.SUFFIXES:

.SUFFIXES: .s65 .o65 .ld65 .ex65

.s65.o65:
	$(CA65) -v -t apple2 -I $(dir $<) -o $@ $<

.o65.ex65:
	$(LD65) -v -C $(filter %.ld65,$^) -o $@ $(filter %.o65,$^)





.PHONY: all install uninstall clean mostlyclean distclean dist package



epple2sys.ex65: epple2sys.o65 epple2sys.ld65



dist: epple2sys.ld65 epple2sys.s65 hascmap.s65 configure Makefile.mk
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

RPM := /usr/src/rpm
TMP := /tmp/rpm

ARCH := noarch

package: $(NAME).spec dist
	cp $(NAME)-$(VERSION).tar.gz $(RPM)/SOURCES
	mkdir -p $(TMP)
	echo "%_prefix $(PREFIX)" >.rpmmacros
	HOME=$(realpath .) rpmbuild -ba --clean --buildroot $(TMP) $<
	fakeroot alien $(RPM)/RPMS/$(ARCH)/$(NAME)-$(VERSION)-1.$(ARCH).rpm
