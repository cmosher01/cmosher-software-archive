NAME = apple2dos
VERSION = 1.0
ARCH = noarch

DIST = $(NAME)-$(VERSION)

.s65.o65:
	mkdir -p $(@D)
	$(CA65) -v -t apple2 -o $@ -I $(<D) -I $(INCDIR) $(CA65DEFS) $<

.o65.ex65:
	mkdir -p $(@D)
	$(LD65) -v -C $(filter %.ld65,$^) -o $@ $(filter %.o65,$^)

.ex65.d13:
	mkdir -p $(@D)
	dd bs=256 skip=27 <$< >$@
	dd bs=256 count=27 <$< >>$@
	dd bs=256 count=184 </dev/zero >>$@
	a2catalog --dos=$(VERSION) >>$@
	dd bs=256 count=221 </dev/zero >>$@

.d13.nib:
	mkdir -p $(@D)
	a2nibblize <$< >$@

.ex65.do:
	mkdir -p $(@D)
	dd bs=256 skip=27 <$< >$@
	dd bs=256 count=27 <$< >>$@
	dd bs=256 count=235 </dev/zero >>$@
	a2catalog --dos=$(VERSION) >>$@
	dd bs=256 count=272 </dev/zero >>$@

.do.nib:
	mkdir -p $(@D)
	a2nibblize <$< >$@

.SUFFIXES: .s65 .o65 .ld65 .ex65 .d13 .do .nib .wxs .wixobj .msi

.PHONY: all install clean


ifdef WINDOWS

.wxs.wixobj:
	VPATH=$(VPATH) $(CANDLE) $< -out $@

.wixobj.msi:
	$(LIGHT) $< -out $@
	cp $@ $(basename $@)-$(VERSION)$(suffix $@)

apple2dos.msi: apple2dos.wixobj all

apple2dos.wixobj: apple2dos.wxs

endif



all: \
	13sector/controller/disk2.ex65 \
	13sector/software/dos310/dos.ex65 \
	13sector/software/dos320/dos.ex65 \
	13sector/software/dos321/dos.ex65 \
	13sector/disks/dos310/clean310.nib \
	13sector/disks/dos320/clean320.nib \
	13sector/disks/dos321/clean321.nib \
	16sector/controller/disk2.ex65 \
	16sector/software/dos330/dos.ex65 \
	16sector/software/dos331/dos.ex65 \
	16sector/software/dos332/dos.ex65 \
	16sector/disks/dos330/clean330.nib \
	16sector/disks/dos331/clean331.nib \
	16sector/disks/dos332/clean332.nib


.PRECIOUS: %.d13 %.do

MODS = dos reloc main filemgr boot1 boot2 rwts rwtsapi




.INTERMEDIATE: 13sector/controller/disk2.s65
13sector/controller/disk2.ex65: CA65DEFS = -D VERSION=13
13sector/controller/disk2.ex65: 13sector/controller/disk2.o65 controller/disk2.ld65
13sector/controller/disk2.s65: controller/disk2.s65
	mkdir -p $(@D)
	cp $< $@



13sector/disks/dos310/clean310.nib: 13sector/disks/dos310/clean310.d13
OBJS_310 = $(foreach mod,$(MODS),13sector/software/dos310/$(mod).o65)
13sector/software/dos310/dos.ex65: CA65DEFS = -D VERSION=310
13sector/software/dos310/dos.ex65: $(OBJS_310) software/dos.ld65
13sector/software/dos310/%.s65: software/%.s65 software/zpabs.s65 software/symbols.s65
	mkdir -p $(@D)
	cp $? $(@D)

.INTERMEDIATE: 13sector/disks/dos310/clean310.ex65
13sector/disks/dos310/clean310.nib: VERSION = 310
13sector/disks/dos310/clean310.ex65: 13sector/software/dos310/dos.ex65
	mkdir -p $(@D)
	cp $? $@

13sector/disks/dos320/clean320.nib: 13sector/disks/dos320/clean320.d13
OBJS_320 = $(foreach mod,$(MODS),13sector/software/dos320/$(mod).o65)
13sector/software/dos320/dos.ex65: CA65DEFS = -D VERSION=320
13sector/software/dos320/dos.ex65: $(OBJS_320) software/dos.ld65
13sector/software/dos320/%.s65: software/%.s65 software/zpabs.s65 software/symbols.s65
	mkdir -p $(@D)
	cp $? $(@D)

.INTERMEDIATE: 13sector/disks/dos320/clean320.ex65
13sector/disks/dos320/clean320.nib: VERSION = 320
13sector/disks/dos320/clean320.ex65: 13sector/software/dos320/dos.ex65
	mkdir -p $(@D)
	cp $? $@

13sector/disks/dos321/clean321.nib: 13sector/disks/dos321/clean321.d13
OBJS_321 = $(foreach mod,$(MODS),13sector/software/dos321/$(mod).o65)
13sector/software/dos321/dos.ex65: CA65DEFS = -D VERSION=321
13sector/software/dos321/dos.ex65: $(OBJS_321) software/dos.ld65
13sector/software/dos321/%.s65: software/%.s65 software/zpabs.s65 software/symbols.s65
	mkdir -p $(@D)
	cp $? $(@D)

.INTERMEDIATE: 13sector/disks/dos321/clean321.ex65
13sector/disks/dos321/clean321.nib: VERSION = 321
13sector/disks/dos321/clean321.ex65: 13sector/software/dos321/dos.ex65
	mkdir -p $(@D)
	cp $? $@



.INTERMEDIATE: 16sector/controller/disk2.s65
16sector/controller/disk2.ex65: CA65DEFS = -D VERSION=16
16sector/controller/disk2.ex65: 16sector/controller/disk2.o65 controller/disk2.ld65
16sector/controller/disk2.s65: controller/disk2.s65
	mkdir -p $(@D)
	cp $< $@

16sector/disks/dos330/clean330.nib: 16sector/disks/dos330/clean330.do
OBJS_330 = $(foreach mod,$(MODS),16sector/software/dos330/$(mod).o65)
16sector/software/dos330/dos.ex65: CA65DEFS = -D VERSION=330
16sector/software/dos330/dos.ex65: $(OBJS_330) software/dos.ld65
16sector/software/dos330/%.s65: software/%.s65 software/zpabs.s65 software/symbols.s65
	mkdir -p $(@D)
	cp $? $(@D)

.INTERMEDIATE: 16sector/disks/dos330/clean330.ex65
16sector/disks/dos330/clean330.nib: VERSION = 330
16sector/disks/dos330/clean330.ex65: 16sector/software/dos330/dos.ex65
	mkdir -p $(@D)
	cp $? $@

16sector/disks/dos331/clean331.nib: 16sector/disks/dos331/clean331.do
OBJS_331 = $(foreach mod,$(MODS),16sector/software/dos331/$(mod).o65)
16sector/software/dos331/dos.ex65: CA65DEFS = -D VERSION=331
16sector/software/dos331/dos.ex65: $(OBJS_331) software/dos.ld65
16sector/software/dos331/%.s65: software/%.s65 software/zpabs.s65 software/symbols.s65
	mkdir -p $(@D)
	cp $? $(@D)

.INTERMEDIATE: 16sector/disks/dos331/clean331.ex65
16sector/disks/dos331/clean331.nib: VERSION = 331
16sector/disks/dos331/clean331.ex65: 16sector/software/dos331/dos.ex65
	mkdir -p $(@D)
	cp $? $@

16sector/disks/dos332/clean332.nib: 16sector/disks/dos332/clean332.do
OBJS_332 = $(foreach mod,$(MODS),16sector/software/dos332/$(mod).o65)
16sector/software/dos332/dos.ex65: CA65DEFS = -D VERSION=332
16sector/software/dos332/dos.ex65: $(OBJS_332) software/dos.ld65
16sector/software/dos332/%.s65: software/%.s65 software/zpabs.s65 software/symbols.s65
	mkdir -p $(@D)
	cp $? $(@D)

.INTERMEDIATE: 16sector/disks/dos332/clean332.ex65
16sector/disks/dos332/clean332.nib: VERSION = 332
16sector/disks/dos332/clean332.ex65: 16sector/software/dos332/dos.ex65
	mkdir -p $(@D)
	cp $? $@





install:
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/controller
	cp 13sector/controller/disk2.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/controller
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/software/dos310
	cp 13sector/software/dos310/dos.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/software/dos310
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/software/dos320
	cp 13sector/software/dos320/dos.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/software/dos320
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/software/dos321
	cp 13sector/software/dos321/dos.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/software/dos321

	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos310
	cp $(VPATH)/disks/README $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks
	cp $(VPATH)/disks/dos310/*.d13 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos310
	cp $(VPATH)/disks/dos310/*.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos310
	cp 13sector/disks/dos310/clean310.d13 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos310
	cp 13sector/disks/dos310/clean310.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos310
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos320
	cp $(VPATH)/disks/dos320/*.d13 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos320
	cp $(VPATH)/disks/dos320/*.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos320
	cp 13sector/disks/dos320/clean320.d13 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos320
	cp 13sector/disks/dos320/clean320.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos320
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos321
	cp $(VPATH)/disks/dos321/*.d13 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos321
	cp $(VPATH)/disks/dos321/*.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos321
	cp 13sector/disks/dos321/clean321.d13 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos321
	cp 13sector/disks/dos321/clean321.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/13sector/disks/dos321

	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/controller
	cp 16sector/controller/disk2.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/controller
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/software/dos330
	cp 16sector/software/dos330/dos.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/software/dos330
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/software/dos331
	cp 16sector/software/dos331/dos.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/software/dos331
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/software/dos332
	cp 16sector/software/dos332/dos.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/software/dos332

	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos330
	cp $(VPATH)/disks/README $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks
	cp $(VPATH)/disks/dos330/*.do $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos330
	cp $(VPATH)/disks/dos330/*.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos330
	cp 16sector/disks/dos330/clean330.do $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos330
	cp 16sector/disks/dos330/clean330.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos330
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos331
	cp $(VPATH)/disks/dos331/*.do $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos331
	cp $(VPATH)/disks/dos331/*.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos331
	cp 16sector/disks/dos331/clean331.do $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos331
	cp 16sector/disks/dos331/clean331.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos331
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos332
	cp $(VPATH)/disks/dos332/*.do $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos332
	cp $(VPATH)/disks/dos332/*.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos332
	cp 16sector/disks/dos332/clean332.do $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos332
	cp 16sector/disks/dos332/clean332.nib $(DESTDIR)/$(PREFIX)/lib/apple2/dos3x/16sector/disks/dos332





dist:	\
	configure \
	Makefile.mk \
	apple2dos.spec \
	apple2dos.wxs \
	a2cdt.jar \
	software/boot2.s65 \
	software/README \
	software/rwtsapi.s65 \
	software/zpabs.s65 \
	software/filemgr.s65 \
	software/reloc.s65 \
	software/dos.s65 \
	software/rwts.s65 \
	software/main.s65 \
	software/boot1.s65 \
	software/symbols.s65 \
	software/dos.ld65 \
	include/macros/reverse.s65 \
	include/macros/hascmap.s65 \
	include/macros/asciihl.s65 \
	controller/disk2.ld65 \
	controller/disk2.s65 \
	disks/README \
	disks/dos320/original32sysmaspls.nib \
	disks/dos320/clean32sysmaspls.nib \
	disks/dos320/README \
	disks/dos320/stock32init.d13 \
	disks/dos320/original32sysmasstd.nib \
	disks/dos320/original32sysmasstd.d13 \
	disks/dos320/clean32sysmaspls.d13 \
	disks/dos320/original32sysmaspls.d13 \
	disks/dos320/clean32sysmasstd.nib \
	disks/dos320/clean32sysmasstd.d13 \
	disks/dos320/stock32mastercreated.d13 \
	disks/dos320/stock32mastercreated.nib \
	disks/dos320/stock32init.nib \
	disks/dos330/stock330init.do \
	disks/dos330/README \
	disks/dos330/original330sysmas.nib \
	disks/dos330/clean330sysmas.nib \
	disks/dos330/stock330mastercreated.do \
	disks/dos330/stock330mastercreated.nib \
	disks/dos330/stock330init.nib \
	disks/dos330/original330sysmas.do \
	disks/dos330/clean330sysmas.do \
	disks/dos310/README \
	disks/dos310/stock31mastercreated.nib \
	disks/dos310/stock31mastercreated.d13 \
	disks/dos310/original31sysmas.d13 \
	disks/dos310/original31sysmas.nib \
	disks/dos310/clean31sysmas_stock_rawdos.nib \
	disks/dos310/stock31init.nib \
	disks/dos310/stock31init.d13 \
	disks/dos310/clean31sysmas_stock_rawdos.d13 \
	disks/dos332/README \
	disks/dos332/stock332init.do \
	disks/dos332/original332sysmas.do \
	disks/dos332/stock332mastercreated.do \
	disks/dos332/stock332mastercreated.nib \
	disks/dos332/clean332sysmas.nib \
	disks/dos332/clean332sysmas.do \
	disks/dos332/stock332init.nib \
	disks/dos332/original332sysmas.nib \
	disks/dos321/original321sysmaspls.d13 \
	disks/dos321/stock321init.d13 \
	disks/dos321/clean321sysmaspls.nib \
	disks/dos321/README \
	disks/dos321/clean321sysmasstd.nib \
	disks/dos321/clean321sysmasstd.d13 \
	disks/dos321/original321sysmasstd.nib \
	disks/dos321/original321sysmaspls.nib \
	disks/dos321/stock321init.nib \
	disks/dos321/clean321sysmaspls.d13 \
	disks/dos321/stock321mastercreated.nib \
	disks/dos321/original321sysmasstd.d13 \
	disks/dos321/stock321mastercreated.d13 \
	disks/dos331/stock331mastercreated.do \
	disks/dos331/README \
	disks/dos331/stock331init.do \
	disks/dos331/clean331sysmas.nib \
	disks/dos331/stock331init.nib \
	disks/dos331/stock331mastercreated.nib \
	disks/dos331/clean331sysmas.do \
	disks/dos331/original331sysmas.nib \
	disks/dos331/original331sysmas.do
	rm -Rf $(DIST)
	mkdir $(DIST)
	rm -f $(DIST)/tmp.tar
	tar -cf $(DIST)/tmp.tar -T /dev/null
	cd $(DIST) ; \
	TARDIR=$$PWD ; \
	cd $(VPATH) ; \
	echo "$^" | tr " " "\n" | xargs -I @ find . -samefile @| tar -rf $$TARDIR/tmp.tar -T -
	cd $(DIST) ; tar xf tmp.tar
	rm $(DIST)/tmp.tar
	rm -f $(DIST).tar.gz
	tar czf $(DIST).tar.gz $(DIST)
	rm -Rf $(DIST)

RPM = $(abspath rpm)

package: $(NAME).spec dist
	mkdir -p $(RPM)/BUILD
	mkdir -p $(RPM)/BUILDROOT
	mkdir -p $(RPM)/RPMS
	mkdir -p $(RPM)/SOURCES
	mkdir -p $(RPM)/SPECS
	mkdir -p $(RPM)/SRPMS
	mkdir -p $(RPM)/VPATH
	cp $(NAME)-$(VERSION).tar.gz $(RPM)/SOURCES
	touch .rpmmacros
	echo "%_prefix $(PREFIX)" >>$(RPM)/.rpmmacros
	echo "%_topdir $(RPM)" >>$(RPM)/.rpmmacros
	HOME=$(RPM) rpmbuild -ba --clean --nodeps --buildroot $(RPM)/BUILDROOT $<
	fakeroot alien $(RPM)/RPMS/$(ARCH)/$(NAME)-$(VERSION)-1.$(ARCH).rpm
