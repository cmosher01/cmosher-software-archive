.m4.s65:
	m4 --traditional $< >$@

.s65.o65:
	xa -C -M -R -c -bt 0 -bd 0 -bb 0 -bz 0 -o $@ $<

.o65.r65:
	ldo65 -bt $(AD_TEXT) -bd 0 -bb 0 -bz 0 -o $@ $^

.r65.e65:
	reloc65 -xt -o $@ $<

.SUFFIXES:
.SUFFIXES: .m4 .s65 .o65 .r65 .e65

all: apple1mon.e65
apple1mon.r65: AD_TEXT=65280
apple1mon.s65: apple1mon.m4 asm.im4

clean:
	rm -fv *.e65 *.r65 *.o65 *.s65
