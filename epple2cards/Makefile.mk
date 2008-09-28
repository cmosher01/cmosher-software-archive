# Builds Epple ][ card ROMs: stdout, stdin, clock
.SUFFIXES: .s65 .o65 .ld65 .ex65

.s65.o65:
	$(CA65) -v -t apple2 -o $@ $<

.o65.ex65:
	$(LD65) -v -C card.ld65 -o $@ $^





.PHONY: all install clean



all: stdout.ex65 stdin.ex65 clock.ex65





install:
	mkdir -p $(PREFIX)/lib/epple2/cards/
	cp stdout.ex65 $(PREFIX)/lib/apple2/cards
	cp stdin.ex65 $(PREFIX)/lib/apple2/cards
	cp clock.ex65 $(PREFIX)/lib/apple2/cards





clean:
	find . -name "*.ex65" | xargs rm -fv
	find . -name "*.o65" | xargs rm -fv
