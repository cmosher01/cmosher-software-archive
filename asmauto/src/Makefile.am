#@configure_input@
#!/usr/bin/make -f

AUTOMAKE_OPTIONS=-Wno-override

ZEROSEGS=-bd 0 -bb 0 -bz 0

.m4.s65:
	$(M4) -I $(srcdir) $(M4FLAGS) $< >$@

.s65.o65:
	$(XA65) -C -M -R -c -bt 0 $(ZERO_SEGS) -o $@ $(XA65FLAGS) $<

.o65.r65:
	$(LDO65) -bt $(CODE_SEG) $(ZERO_SEGS) -o $@ $(LD65FLAGS) $^

.r65.a65:
	$(RELOC65) -xt -o $@ $(RELOC65FLAGS) $<

mypkgdir=$(prefix)/lib/epple2/system

mypkg_DATA=epple2sys.a65
noinst_DATA=epple2sys.md5
EXTRA_DIST=epple2sys.m4 asm.m4h epple2sys.md5

epple2sys.r65: CODE_SEG=64512
epple2sys.s65: epple2sys.m4 asm.m4h

define_new_md5: all
	$(MD5SUM) --binary epple2sys.a65 >$(srcdir)/epple2sys.md5

check_SCRIPTS=unit_test.sh
TESTS=$(check_SCRIPTS)

unit_test.sh:
	touch $@
	if test "X$(MD5SUM)" == X ; \
	then \
		echo "echo \"md5sum program not found, so cannot run this test\" >&2" >>$@ ; \
		echo "exit 1" >>$@ ; \
	else \
		echo "$(MD5SUM) --check --status --warn $(srcdir)/epple2sys.md5" >>$@ ; \
	fi
	chmod +x $@



CLEANFILES=unit_test.sh epple2sys.a65 epple2sys.r65 epple2sys.s65
