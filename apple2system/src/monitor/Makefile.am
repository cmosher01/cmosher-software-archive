#@configure_input@

AUTOMAKE_OPTIONS=-Wno-override

ZEROSEGS=-bd 0 -bb 0 -bz 0

.m4.s65:
	$(M4) -I $(srcdir) -I $(srcdir)/../include $(M4FLAGS) $< >$@

.s65.o65:
	$(XA65) -C -M -R -c -bt 0 $(ZERO_SEGS) -o $@ $(XA65FLAGS) $<

.o65.r65:
	$(LDO65) -bt $(CODE_SEG) $(ZERO_SEGS) -o $@ $(LD65FLAGS) $^

.r65.a65:
	$(RELOC65) -xt -o $@ $(RELOC65FLAGS) $<

M4S=lores.m4 disasm.m4 debug.m4 paddles.m4 display1.m4 math.m4 display2.m4 cassette.m4 keyin.m4 cmd.m4 vectors.m4
OBJS=lores.o65 disasm.o65 debug.o65 paddles.o65 display1.o65 math.o65 display2.o65 cassette.o65 keyin.o65 cmd.o65 vectors.o65

mypkgdir=$(prefix)/lib/apple2/system

mypkg_DATA=monitor2.a65 monitor2plus.a65
noinst_DATA=monitor2.md5 monitor2plus.md5
EXTRA_DIST=$(M4S) $(noinst_DATA)



monitor2.r65: monitor2.o65 $(OBJS)
monitor2.r65: M4FLAGS=-DVERSION=1

#monitor2.r65: CODE_SEG=63488 M4FLAGS=-DVERSION=1

monitor2plus.r65: monitor2plus.o65 $(OBJS)
monitor2plus.r65: CODE_SEG=63488 M4FLAGS=-DVERSION=2

lores.o65: lores.s65 symbols.m4h ../include/asm.m4h
disasm.o65: disasm.s65 symbols.m4h ../include/asm.m4h
debug.o65: debug.s65 symbols.m4h ../include/asm.m4h
paddles.o65: paddles.s65 symbols.m4h ../include/asm.m4h
display1.o65: display1.s65 symbols.m4h ../include/asm.m4h
math.o65: math.s65 symbols.m4h ../include/asm.m4h
display2.o65: display2.s65 symbols.m4h ../include/asm.m4h
cassette.o65: cassette.s65 symbols.m4h ../include/asm.m4h
keyin.o65: keyin.s65 symbols.m4h ../include/asm.m4h
cmd.o65: cmd.s65 symbols.m4h ../include/asm.m4h
vectors.o65: vectors.s65 symbols.m4h ../include/asm.m4h

define_new_md5: all
	$(MD5SUM) --binary monitor2.a65 >$(srcdir)/monitor2.md5
	$(MD5SUM) --binary monitor2plus.a65 >$(srcdir)/monitor2plus.md5

check_SCRIPTS=unit_test.sh
TESTS=$(check_SCRIPTS)

unit_test.sh:
	touch $@
	if test "X$(MD5SUM)" == X ; \
	then \
		echo "echo \"md5sum program not found, so cannot run this test\" >&2" >>$@ ; \
		echo "exit 1" >>$@ ; \
	else \
		echo "$(MD5SUM) --check --status --warn $(srcdir)/monitor2.md5" >>$@ ; \
		echo "$(MD5SUM) --check --status --warn $(srcdir)/monitor2plus.md5" >>$@ ; \
	fi
	chmod +x $@



CLEANFILES=unit_test.sh *.a65 *.r65 *.s65 *.o65
