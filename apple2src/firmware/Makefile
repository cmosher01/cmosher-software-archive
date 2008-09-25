CA65 = ca65
LD65 = ld65
CP = cp

.SUFFIXES: .s65 .o65 .ld65 .rom

.s65.o65:
	$(CA65) -v -t apple2 -o $@ -I $(<D) -I $(INCDIR) $(CA65DEFS) $<

.o65.rom:
	$(LD65) -v -C $(filter %.ld65,$^) -o $@ $(filter %.o65,$^)

SUBDIRS = \
	intbasic \
	disk2/13sector \
	disk2/16sector \
	applesoft \
	other \
	monitor/apple2 \
	monitor/apple2plus

.PHONY: all subdirs clean $(SUBDIRS)





all: \
	subdirs \
	intbasic/intbasic.rom \
	disk2/13sector/disk2.rom \
	disk2/16sector/disk2.rom \
	applesoft/applesoft.rom \
	other/other.rom \
	monitor/apple2/monitor.rom \
	monitor/apple2plus/monitor.rom



subdirs: $(SUBDIRS)
	mkdir -p $^



intbasic/intbasic.rom: CA65DEFS = -D VERSION=2 -D BUGFIX
intbasic/intbasic.rom: intbasic/intbasic.o65 intbasic/intbasic.ld65



disk2/13sector/disk2.rom: CA65DEFS = -D VERSION=13 -D NODELAY
disk2/13sector/disk2.rom: disk2/13sector/disk2.o65 disk2/disk2.ld65

disk2/13sector/disk2.s65: disk2/disk2.s65
	$(CP) $< $@

disk2/16sector/disk2.rom: CA65DEFS = -D VERSION=16 -D NODELAY
disk2/16sector/disk2.rom: disk2/16sector/disk2.o65 disk2/disk2.ld65

disk2/16sector/disk2.s65: disk2/disk2.s65
	$(CP) $< $@



applesoft/applesoft.rom: applesoft/applesoft.o65 applesoft/applesoft.ld65



OTHER_MODS = other fp1 miniasm1 fp2 miniasm2 f669 sweet16

OTHER_OBJS = $(foreach mod,$(OTHER_MODS),other/$(mod).o65)

other/other.rom: $(OTHER_OBJS) other/other.ld65



MONITOR_MODS = lores disasm debug paddles display1 math display2 cassette keyin monitor vectors

MONITOR2_OBJS = $(foreach mod,$(MONITOR_MODS),monitor/apple2/$(mod).o65)

MONITOR2P_OBJS = $(foreach mod,$(MONITOR_MODS),monitor/apple2plus/$(mod).o65)

monitor/apple2/monitor.rom: CA65DEFS = -D VERSION=1
monitor/apple2/monitor.rom: $(MONITOR2_OBJS) monitor/monitor.ld65

monitor/apple2/%.s65: monitor/%.s65 monitor/symbols.s65
	$(CP) $? $(@D)

monitor/apple2plus/monitor.rom: CA65DEFS = -D VERSION=2
monitor/apple2plus/monitor.rom: $(MONITOR2P_OBJS) monitor/monitor.ld65

monitor/apple2plus/%.s65: monitor/%.s65 monitor/symbols.s65
	$(CP) $? $(@D)





clean:
	find . -name "*.rom" | xargs rm -fv
	find . -name "*.o65" | xargs rm -fv
