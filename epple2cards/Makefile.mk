# Builds Epple ][ card ROMs: stdout, stdin, clock

NAME = epple2cards
VERSION = 1.0
ARCH = noarch

DIST = $(NAME)-$(VERSION)

.SUFFIXES:

.SUFFIXES: .s65 .o65 .ld65 .ex65 .wxs .wixobj .msi

.s65.o65:
	$(CA65) -v -t apple2 -o $@ $<

.o65.ex65:
	$(LD65) -v -C $(filter %.ld65,$^) -o $@ $(filter %.o65,$^)





.PHONY: all install clean



ifdef WINDOWS

.wxs.wixobj:
	$(CANDLE) $< -out $@

.wixobj.msi:
	$(LIGHT) $< -out $@

epple2cards.msi: epple2cards.wixobj all

epple2cards.wixobj: epple2cards.wxs

endif



all: stdout.ex65 stdin.ex65 clock.ex65
stdout.ex65: stdout.o65 card.ld65
stdin.ex65: stdin.o65 card.ld65
clock.ex65: clock.o65 card.ld65



dist: card.ld65  clock.s65  configure  Makefile.mk  stdin.s65  stdout.s65 epple2cards.spec
	rm -fv $(DIST).tar.gz
	rm -Rfv $(DIST)
	mkdir -v $(DIST)
	cp -v $^ $(DIST)
	tar cvzf $(DIST).tar.gz $(DIST)
	rm -Rf $(DIST)



install: all
	mkdir -pv $(DESTDIR)$(PREFIX)/lib/epple2/cards/
	cp -v stdout.ex65 $(DESTDIR)$(PREFIX)/lib/epple2/cards
	cp -v stdin.ex65 $(DESTDIR)$(PREFIX)/lib/epple2/cards
	cp -v clock.ex65 $(DESTDIR)$(PREFIX)/lib/epple2/cards



uninstall:
	rm -fv $(DESTDIR)$(PREFIX)/lib/epple2/cards/stdout.ex65
	rm -fv $(DESTDIR)$(PREFIX)/lib/epple2/cards/stdin.ex65
	rm -fv $(DESTDIR)$(PREFIX)/lib/epple2/cards/clock.ex65
	rmdir -pv $(DESTDIR)$(PREFIX)/lib/epple2/cards/



clean:
	find . -name "*.ex65" | xargs rm -fv
	find . -name "*.o65" | xargs rm -fv
	rm -fv $(DIST).tar.gz
	rm -Rfv $(DIST)



mostlyclean: clean



distclean: clean
	rm -fv Makefile

RPM = $(abspath rpm)

package: $(NAME).spec dist
	mkdir -p $(RPM)/{BUILD,BUILDROOT,RPMS,SOURCES,SPECS,SRPMS,VPATH}
	cp $(NAME)-$(VERSION).tar.gz $(RPM)/SOURCES
	touch .rpmmacros
	echo "%_prefix $(PREFIX)" >>$(RPM)/.rpmmacros
	echo "%_topdir $(RPM)" >>$(RPM)/.rpmmacros
	HOME=$(RPM) rpmbuild -ba --clean --nodeps --buildroot $(RPM)/BUILDROOT $<
	fakeroot alien $(RPM)/RPMS/$(ARCH)/$(NAME)-$(VERSION)-1.$(ARCH).rpm
