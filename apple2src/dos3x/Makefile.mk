CDT = java -cp "$(A2CDT)"

.s65.o65:
	$(CA65) -v -t apple2 -o $@ -I $(<D) -I $(INCDIR) $(CA65DEFS) $<

.o65.ex65:
	$(LD65) -v -C $(filter %.ld65,$^) -o $@ $(filter %.o65,$^)

.ex65.d13:
	$(CDT) dd --skip=0x1B00 <$< >$@
	$(CDT) dd --count=0x1B00 <$< >>$@
	$(CDT) dd --count=0xB800 --const=0 >>$@
	$(CDT) CreateCatalog --version=$(VERSION) >>$@
	$(CDT) dd --count=0xDD00 --const=0 >>$@

.d13.nib:
	$(CDT) ConvertD13toNibble <$< >$@

.ex65.do:
	$(CDT) dd --skip=0x1B00 <$< >$@
	$(CDT) dd --count=0x1B00 <$< >>$@
	$(CDT) dd --count=0xEB00 --const=0 >>$@
	$(CDT) CreateCatalog --version=$(VERSION) >>$@
	$(CDT) dd --count=0x11000 --const=0 >>$@

.do.nib:
	$(CDT) ConvertD16toNibble <$< >$@

.SUFFIXES: .s65 .o65 .ld65 .ex65 .d13 .do .nib

SUBDIRS = \
	13sector \
	13sector/controller \
	13sector/software \
	13sector/software/dos310 \
	13sector/software/dos320 \
	13sector/software/dos321 \
	13sector/disks \
	13sector/disks/dos310 \
	13sector/disks/dos320 \
	13sector/disks/dos321 \
	16sector \
	16sector/controller \
	16sector/software \
	16sector/software/dos330 \
	16sector/software/dos331 \
	16sector/software/dos332 \
	16sector/disks \
	16sector/disks/dos330 \
	16sector/disks/dos331 \
	16sector/disks/dos332

.PHONY: all subdirs $(SUBDIRS) install clean





all: \
	subdirs \
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



subdirs: $(SUBDIRS)
	mkdir -p $^


.PRECIOUS: %.d13 %.do

MODS = dos reloc main filemgr boot1 boot2 rwts rwtsapi




.INTERMEDIATE: 13sector/controller/disk2.s65
13sector/controller/disk2.ex65: CA65DEFS = -D VERSION=13 -D NODELAY
13sector/controller/disk2.ex65: 13sector/controller/disk2.o65 controller/disk2.ld65
13sector/controller/disk2.s65: controller/disk2.s65
	cp $< $@



13sector/disks/dos310/clean310.nib: 13sector/disks/dos310/clean310.d13
OBJS_310 = $(foreach mod,$(MODS),13sector/software/dos310/$(mod).o65)
13sector/software/dos310/dos.ex65: CA65DEFS = -D VERSION=310 -D NODELAY
13sector/software/dos310/dos.ex65: $(OBJS_310) software/dos.ld65
13sector/software/dos310/%.s65: software/%.s65 software/zpabs.s65 software/symbols.s65
	cp $? $(@D)

.INTERMEDIATE: 13sector/disks/dos310/clean310.ex65
13sector/disks/dos310/clean310.nib: VERSION = 310
13sector/disks/dos310/clean310.ex65: 13sector/software/dos310/dos.ex65
	cp $? $@

13sector/disks/dos320/clean320.nib: 13sector/disks/dos320/clean320.d13
OBJS_320 = $(foreach mod,$(MODS),13sector/software/dos320/$(mod).o65)
13sector/software/dos320/dos.ex65: CA65DEFS = -D VERSION=320 -D NODELAY
13sector/software/dos320/dos.ex65: $(OBJS_320) software/dos.ld65
13sector/software/dos320/%.s65: software/%.s65 software/zpabs.s65 software/symbols.s65
	cp $? $(@D)

.INTERMEDIATE: 13sector/disks/dos320/clean320.ex65
13sector/disks/dos320/clean320.nib: VERSION = 320
13sector/disks/dos320/clean320.ex65: 13sector/software/dos320/dos.ex65
	cp $? $@

13sector/disks/dos321/clean321.nib: 13sector/disks/dos321/clean321.d13
OBJS_321 = $(foreach mod,$(MODS),13sector/software/dos321/$(mod).o65)
13sector/software/dos321/dos.ex65: CA65DEFS = -D VERSION=321 -D NODELAY
13sector/software/dos321/dos.ex65: $(OBJS_321) software/dos.ld65
13sector/software/dos321/%.s65: software/%.s65 software/zpabs.s65 software/symbols.s65
	cp $? $(@D)

.INTERMEDIATE: 13sector/disks/dos321/clean321.ex65
13sector/disks/dos321/clean321.nib: VERSION = 321
13sector/disks/dos321/clean321.ex65: 13sector/software/dos321/dos.ex65
	cp $? $@



.INTERMEDIATE: 16sector/controller/disk2.s65
16sector/controller/disk2.ex65: CA65DEFS = -D VERSION=16 -D NODELAY
16sector/controller/disk2.ex65: 16sector/controller/disk2.o65 controller/disk2.ld65
16sector/controller/disk2.s65: controller/disk2.s65
	cp $< $@

16sector/disks/dos330/clean330.nib: 16sector/disks/dos330/clean330.do
OBJS_330 = $(foreach mod,$(MODS),16sector/software/dos330/$(mod).o65)
16sector/software/dos330/dos.ex65: CA65DEFS = -D VERSION=330 -D NODELAY
16sector/software/dos330/dos.ex65: $(OBJS_330) software/dos.ld65
16sector/software/dos330/%.s65: software/%.s65 software/zpabs.s65 software/symbols.s65
	cp $? $(@D)

.INTERMEDIATE: 16sector/disks/dos330/clean330.ex65
16sector/disks/dos330/clean330.nib: VERSION = 330
16sector/disks/dos330/clean330.ex65: 16sector/software/dos330/dos.ex65
	cp $? $@

16sector/disks/dos331/clean331.nib: 16sector/disks/dos331/clean331.do
OBJS_331 = $(foreach mod,$(MODS),16sector/software/dos331/$(mod).o65)
16sector/software/dos331/dos.ex65: CA65DEFS = -D VERSION=331 -D NODELAY
16sector/software/dos331/dos.ex65: $(OBJS_331) software/dos.ld65
16sector/software/dos331/%.s65: software/%.s65 software/zpabs.s65 software/symbols.s65
	cp $? $(@D)

.INTERMEDIATE: 16sector/disks/dos331/clean331.ex65
16sector/disks/dos331/clean331.nib: VERSION = 331
16sector/disks/dos331/clean331.ex65: 16sector/software/dos331/dos.ex65
	cp $? $@

16sector/disks/dos332/clean332.nib: 16sector/disks/dos332/clean332.do
OBJS_332 = $(foreach mod,$(MODS),16sector/software/dos332/$(mod).o65)
16sector/software/dos332/dos.ex65: CA65DEFS = -D VERSION=332 -D NODELAY
16sector/software/dos332/dos.ex65: $(OBJS_332) software/dos.ld65
16sector/software/dos332/%.s65: software/%.s65 software/zpabs.s65 software/symbols.s65
	cp $? $(@D)

.INTERMEDIATE: 16sector/disks/dos332/clean332.ex65
16sector/disks/dos332/clean332.nib: VERSION = 332
16sector/disks/dos332/clean332.ex65: 16sector/software/dos332/dos.ex65
	cp $? $@





install:
	mkdir -p $(PREFIX)/lib/apple2/dos3x/13sector/controller
	cp 13sector/controller/disk2.ex65 $(PREFIX)/lib/apple2/dos3x/13sector/controller
	mkdir -p $(PREFIX)/lib/apple2/dos3x/13sector/software/dos310
	cp 13sector/software/dos310/dos.ex65 $(PREFIX)/lib/apple2/dos3x/13sector/software/dos310
	mkdir -p $(PREFIX)/lib/apple2/dos3x/13sector/software/dos320
	cp 13sector/software/dos320/dos.ex65 $(PREFIX)/lib/apple2/dos3x/13sector/software/dos320
	mkdir -p $(PREFIX)/lib/apple2/dos3x/13sector/software/dos321
	cp 13sector/software/dos321/dos.ex65 $(PREFIX)/lib/apple2/dos3x/13sector/software/dos321

	mkdir -p $(PREFIX)/lib/apple2/dos3x/13sector/disks/dos310
	cp $(VPATH)/disks/README $(PREFIX)/lib/apple2/dos3x/13sector/disks
	cp $(VPATH)/disks/dos310/*.d13 $(PREFIX)/lib/apple2/dos3x/13sector/disks/dos310
	cp $(VPATH)/disks/dos310/*.nib $(PREFIX)/lib/apple2/dos3x/13sector/disks/dos310
	cp 13sector/disks/dos310/clean310.d13 $(PREFIX)/lib/apple2/dos3x/13sector/disks/dos310
	cp 13sector/disks/dos310/clean310.nib $(PREFIX)/lib/apple2/dos3x/13sector/disks/dos310
	mkdir -p $(PREFIX)/lib/apple2/dos3x/13sector/disks/dos320
	cp $(VPATH)/disks/dos320/*.d13 $(PREFIX)/lib/apple2/dos3x/13sector/disks/dos320
	cp $(VPATH)/disks/dos320/*.nib $(PREFIX)/lib/apple2/dos3x/13sector/disks/dos320
	cp 13sector/disks/dos320/clean320.d13 $(PREFIX)/lib/apple2/dos3x/13sector/disks/dos320
	cp 13sector/disks/dos320/clean320.nib $(PREFIX)/lib/apple2/dos3x/13sector/disks/dos320
	mkdir -p $(PREFIX)/lib/apple2/dos3x/13sector/disks/dos321
	cp $(VPATH)/disks/dos321/*.d13 $(PREFIX)/lib/apple2/dos3x/13sector/disks/dos321
	cp $(VPATH)/disks/dos321/*.nib $(PREFIX)/lib/apple2/dos3x/13sector/disks/dos321
	cp 13sector/disks/dos321/clean321.d13 $(PREFIX)/lib/apple2/dos3x/13sector/disks/dos321
	cp 13sector/disks/dos321/clean321.nib $(PREFIX)/lib/apple2/dos3x/13sector/disks/dos321

	mkdir -p $(PREFIX)/lib/apple2/dos3x/16sector/controller
	cp 16sector/controller/disk2.ex65 $(PREFIX)/lib/apple2/dos3x/16sector/controller
	mkdir -p $(PREFIX)/lib/apple2/dos3x/16sector/software/dos330
	cp 16sector/software/dos330/dos.ex65 $(PREFIX)/lib/apple2/dos3x/16sector/software/dos330
	mkdir -p $(PREFIX)/lib/apple2/dos3x/16sector/software/dos331
	cp 16sector/software/dos331/dos.ex65 $(PREFIX)/lib/apple2/dos3x/16sector/software/dos331
	mkdir -p $(PREFIX)/lib/apple2/dos3x/16sector/software/dos332
	cp 16sector/software/dos332/dos.ex65 $(PREFIX)/lib/apple2/dos3x/16sector/software/dos332

	mkdir -p $(PREFIX)/lib/apple2/dos3x/16sector/disks/dos330
	cp $(VPATH)/disks/README $(PREFIX)/lib/apple2/dos3x/16sector/disks
	cp $(VPATH)/disks/dos330/*.do $(PREFIX)/lib/apple2/dos3x/16sector/disks/dos330
	cp $(VPATH)/disks/dos330/*.nib $(PREFIX)/lib/apple2/dos3x/16sector/disks/dos330
	cp 16sector/disks/dos330/clean330.do $(PREFIX)/lib/apple2/dos3x/16sector/disks/dos330
	cp 16sector/disks/dos330/clean330.nib $(PREFIX)/lib/apple2/dos3x/16sector/disks/dos330
	mkdir -p $(PREFIX)/lib/apple2/dos3x/16sector/disks/dos331
	cp $(VPATH)/disks/dos331/*.do $(PREFIX)/lib/apple2/dos3x/16sector/disks/dos331
	cp $(VPATH)/disks/dos331/*.nib $(PREFIX)/lib/apple2/dos3x/16sector/disks/dos331
	cp 16sector/disks/dos331/clean331.do $(PREFIX)/lib/apple2/dos3x/16sector/disks/dos331
	cp 16sector/disks/dos331/clean331.nib $(PREFIX)/lib/apple2/dos3x/16sector/disks/dos331
	mkdir -p $(PREFIX)/lib/apple2/dos3x/16sector/disks/dos332
	cp $(VPATH)/disks/dos332/*.do $(PREFIX)/lib/apple2/dos3x/16sector/disks/dos332
	cp $(VPATH)/disks/dos332/*.nib $(PREFIX)/lib/apple2/dos3x/16sector/disks/dos332
	cp 16sector/disks/dos332/clean332.do $(PREFIX)/lib/apple2/dos3x/16sector/disks/dos332
	cp 16sector/disks/dos332/clean332.nib $(PREFIX)/lib/apple2/dos3x/16sector/disks/dos332





clean:
	rm -Rfv $(SUBDIRS)
