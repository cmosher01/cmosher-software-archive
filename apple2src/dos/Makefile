A2CDT_JAR = a2cdt.jar
A2CDT = java -cp $(A2CDT_JAR)

.SUFFIXES: .ex65 .d13 .do .nib

.ex65.d13:
	$(A2CDT) dd --skip=0x1B00 <$< >$@
	$(A2CDT) dd --count=0x1B00 <$< >>$@
	$(A2CDT) dd --count=0xB800 --const=0 >>$@
	$(A2CDT) CreateCatalog --version=$(VERSION) >>$@
	$(A2CDT) dd --count=0xDD00 --const=0 >>$@

.d13.nib:
	$(A2CDT) ConvertD13toNibble <$< >$@

.ex65.do:
	$(A2CDT) dd --skip=0x1B00 <$< >$@
	$(A2CDT) dd --count=0x1B00 <$< >>$@
	$(A2CDT) dd --count=0xEB00 --const=0 >>$@
	$(A2CDT) CreateCatalog --version=$(VERSION) >>$@
	$(A2CDT) dd --count=0x11000 --const=0 >>$@

.do.nib:
	$(A2CDT) ConvertD16toNibble <$< >$@

disk2/13sector/disk2.ex65: CA65DEFS = -D VERSION=13 -D NODELAY
disk2/13sector/disk2.ex65: disk2/13sector/disk2.o65 disk2/disk2.ld65

disk2/13sector/disk2.s65: disk2/disk2.s65
        $(CP) $< $@

disk2/16sector/disk2.ex65: CA65DEFS = -D VERSION=16 -D NODELAY
disk2/16sector/disk2.ex65: disk2/16sector/disk2.o65 disk2/disk2.ld65

disk2/16sector/disk2.s65: disk2/disk2.s65
        $(CP) $< $@

