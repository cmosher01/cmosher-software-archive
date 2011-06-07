#!/usr/bin/make -f

AUTOMAKE_OPTIONS=-Wno-override

.m4.s65:
	$(M4) -I $(srcdir) $(M4FLAGS) $< >$@

.s65.o65:
	$(XA65) -C -M -R -c -bt 0 -bd 0 -bb 0 -bz 0 -o $@ $(XA65FLAGS) $<

.o65.r65:
	$(LDO65) -bt $(AD_TEXT) -bd 0 -bb 0 -bz 0 -o $@ $(LD65FLAGS) $^

.r65.e65:
	$(RELOC65) -xt -o $@ $(RELOC65FLAGS) $<

bin_PROGRAMS=epple2sys.e65

epple2sys_e65_SOURCES=epple2sys.m4 asm.m4h

epple2sys.e65$(EXEEXT):
epple2sys.r65: AD_TEXT=64512
epple2sys.s65: epple2sys.m4 asm.m4h

CLEANFILES=epple2sys.r65 epple2sys.s65
