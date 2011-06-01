.m4.s65:
	m4 --traditional $< >$@

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

all: x.e65
x.r65: AD_TEXT=2048
x.r65: y.o65 x.o65

clean:
	rm -fv *.e65 *.r65 *.o65 *.s65
