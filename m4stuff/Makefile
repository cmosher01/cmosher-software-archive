.m4.s65:
	m4 --traditional $< >$@
	cat $@

.s65.o65:
	xa -C -R -c -o $@ $<

.o65.r65:
	ldo65 -bt $(AD_TEXT) -bd 0 -bb 0 -bz 0 -o $@ $^
	file65 -V $@

.r65.e65:
	reloc65 -xt -o $@ $<
	xxd $@

.SUFFIXES:
.SUFFIXES: .m4 .s65 .o65 .r65 .e65

all: apple1mon.e65
apple1mon.r65: AD_TEXT=65280
apple1mon.s65: apple1mon.m4 asm.im4

clean:
	rm -fv *.e65 *.r65 *.o65 *.s65
