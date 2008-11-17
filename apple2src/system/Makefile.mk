# Builds Apple ][ and Apple ][ plus system software, which
# includes Monitor, Integer BASIC, misc. routines, and Applesoft BASIC.
#
# user must define INCDIR to directory with necessary include files
#
# The only option for this build (which is not yet implemented as an option)
# is whether to build Integer BASIC original or with bug fixes. Currently,
# it builds the original version without bug fixes.
#
.SUFFIXES: .s65 .o65 .ld65 .ex65

.s65.o65:
	$(CA65) -v -t apple2 -o $@ -I $(<D) -I $(INCDIR) $(CA65DEFS) $<

.o65.ex65:
	$(LD65) -v -C $(filter %.ld65,$^) -o $@ $(filter %.o65,$^)





SUBDIRS = intbasic other applesoft monitor/apple2 monitor/apple2plus



.PHONY: all subdirs $(SUBDIRS) install clean



all: \
	subdirs \
	intbasic/intbasic.ex65 \
	other/other.ex65 \
	applesoft/applesoft.ex65 \
	monitor/apple2/monitor.ex65 \
	monitor/apple2plus/monitor.ex65



subdirs: $(SUBDIRS)
	echo "$(VPATH)"
	mkdir -p $^



intbasic/intbasic.ex65: CA65DEFS = -D VERSION=2
intbasic/intbasic.ex65: intbasic/intbasic.o65 intbasic/intbasic.ld65



OTHER_MODS = other fp1 miniasm1 fp2 miniasm2 f669 sweet16

OTHER_OBJS = $(foreach mod,$(OTHER_MODS),other/$(mod).o65)

other/other.ex65: $(OTHER_OBJS) other/other.ld65



applesoft/applesoft.ex65: applesoft/applesoft.o65 applesoft/applesoft.ld65



MONITOR_MODS = monitor lores disasm debug paddles display1 math display2 cassette keyin cmd vectors

MONITOR2_OBJS = $(foreach mod,$(MONITOR_MODS),monitor/apple2/$(mod).o65)

monitor/apple2/monitor.ex65: CA65DEFS = -D VERSION=1
monitor/apple2/monitor.ex65: $(MONITOR2_OBJS) monitor/monitor.ld65

monitor/apple2/%.s65: monitor/%.s65 monitor/symbols.s65
	cp $? $(@D)



MONITOR2P_OBJS = $(foreach mod,$(MONITOR_MODS),monitor/apple2plus/$(mod).o65)

monitor/apple2plus/monitor.ex65: CA65DEFS = -D VERSION=2
monitor/apple2plus/monitor.ex65: $(MONITOR2P_OBJS) monitor/monitor.ld65

monitor/apple2plus/%.s65: monitor/%.s65 monitor/symbols.s65
	cp $? $(@D)





install:
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/system/monitor/apple2
	cp monitor/apple2/monitor.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/system/monitor/apple2
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/system/monitor/apple2plus
	cp monitor/apple2plus/monitor.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/system/monitor/apple2plus
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/system/intbasic
	cp intbasic/intbasic.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/system/intbasic
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/system/other
	cp other/other.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/system/other
	mkdir -p $(DESTDIR)/$(PREFIX)/lib/apple2/system/applesoft
	cp applesoft/applesoft.ex65 $(DESTDIR)/$(PREFIX)/lib/apple2/system/applesoft





clean:
	find . -name "*.ex65" | xargs rm -fv
	find . -name "*.o65" | xargs rm -fv
	rm -Rfv monitor/apple2 monitor/apple2plus




DIST=apple2sys-1.0
dist:	\
	configure \
	Makefile.mk \
	applesoft/applesoft.s65 \
	applesoft/applesoft.ld65 \
	intbasic/intbasic.s65 \
	intbasic/intbasic.ld65 \
	monitor/cassette.s65 \
	monitor/cmd.s65 \
	monitor/debug.s65 \
	monitor/disasm.s65 \
	monitor/display1.s65 \
	monitor/display2.s65 \
	monitor/keyin.s65 \
	monitor/lores.s65 \
	monitor/math.s65 \
	monitor/monitor.s65 \
	monitor/paddles.s65 \
	monitor/symbols.s65 \
	monitor/vectors.s65 \
	monitor/monitor.ld65 \
	other/f669.s65 \
	other/fp1.s65 \
	other/fp2.s65 \
	other/miniasm1.s65 \
	other/miniasm2.s65 \
	other/other.s65 \
	other/sweet16.s65 \
	other/other.ld65 \
	include/macros/asciihl.s65 \
	include/macros/hascmap.s65 \
	include/macros/reverse.s65
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
