CA65 = ca65
LD65 = ld65
CP = cp

.SUFFIXES: .s65 .o65 .ld65 .rom

.s65.o65:
	$(CA65) -v -t apple2 -o $@ -I $(<D) -I $(INCDIR) $(CA65DEFS) $<

.o65.rom:
	$(LD65) -v -C $(filter %.ld65,$^) -o $@ $(filter %.o65,$^)

SUBDIRS = dos310 dos320 dos321 dos330 dos331 dos332

.PHONY: all subdirs clean $(SUBDIRS)





all: subdirs $(SUBDIRS:=/dos.rom)



subdirs: $(SUBDIRS)
	mkdir -p $^



MODS = reloc dos filemgr boot1 boot2 rwts rwtsapi

OBJS_310 = $(foreach mod,$(MODS),dos310/$(mod).o65)
dos310/dos.rom: CA65DEFS = -D VERSION=310 -D NODELAY
dos310/dos.rom: $(OBJS_310) dos.ld65
dos310/%.s65: %.s65 zpabs.s65 symbols.s65
	$(CP) $? $(@D)

OBJS_320 = $(foreach mod,$(MODS),dos320/$(mod).o65)
dos320/dos.rom: CA65DEFS = -D VERSION=320 -D NODELAY
dos320/dos.rom: $(OBJS_320) dos.ld65
dos320/%.s65: %.s65 zpabs.s65 symbols.s65
	$(CP) $? $(@D)

OBJS_321 = $(foreach mod,$(MODS),dos321/$(mod).o65)
dos321/dos.rom: CA65DEFS = -D VERSION=321 -D NODELAY
dos321/dos.rom: $(OBJS_321) dos.ld65
dos321/%.s65: %.s65 zpabs.s65 symbols.s65
	$(CP) $? $(@D)

OBJS_330 = $(foreach mod,$(MODS),dos330/$(mod).o65)
dos330/dos.rom: CA65DEFS = -D VERSION=330 -D NODELAY
dos330/dos.rom: $(OBJS_330) dos.ld65
dos330/%.s65: %.s65 zpabs.s65 symbols.s65
	$(CP) $? $(@D)

OBJS_331 = $(foreach mod,$(MODS),dos331/$(mod).o65)
dos331/dos.rom: CA65DEFS = -D VERSION=331 -D NODELAY
dos331/dos.rom: $(OBJS_331) dos.ld65
dos331/%.s65: %.s65 zpabs.s65 symbols.s65
	$(CP) $? $(@D)

OBJS_332 = $(foreach mod,$(MODS),dos332/$(mod).o65)
dos332/dos.rom: CA65DEFS = -D VERSION=332 -D NODELAY
dos332/dos.rom: $(OBJS_332) dos.ld65
dos332/%.s65: %.s65 zpabs.s65 symbols.s65
	$(CP) $? $(@D)



clean:
	rm -Rfv $(SUBDIRS)
