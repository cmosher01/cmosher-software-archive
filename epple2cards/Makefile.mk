# Builds Epple ][ card ROMs: stdout, stdin, clock

.SUFFIXES:

.SUFFIXES: .s65 .o65 .ld65 .ex65

.s65.o65:
	$(CA65) -v -t apple2 -o $@ $<

.o65.ex65:
	$(LD65) -v -C $(filter %.ld65,$^) -o $@ $(filter %.o65,$^)





.PHONY: all install clean



all: stdout.ex65 stdin.ex65 clock.ex65
stdout.ex65: stdout.o65 card.ld65
stdin.ex65: stdin.o65 card.ld65
clock.ex65: clock.o65 card.ld65



DIST=epple2cards-1.0
dist: card.ld65  clock.s65  configure  Makefile.mk  stdin.s65  stdout.s65
	rm -fv $(DIST).tar.gz
	rm -Rfv $(DIST)
	mkdir -v $(DIST)
	cp -v $^ $(DIST)
	tar cvzf $(DIST).tar.gz $(DIST)



install: all
	mkdir -pv $(DESTDIR)$(PREFIX)/lib/epple2/cards/
	cp -v stdout.ex65 $(DESTDIR)$(PREFIX)/lib/epple2/cards
	cp -v stdin.ex65 $(DESTDIR)$(PREFIX)/lib/epple2/cards
	cp -v clock.ex65 $(DESTDIR)$(PREFIX)/lib/epple2/cards



uninstall:
	rm -fv $(DESTDIR)$(PREFIX)/lib/epple2/cards/stdout.ex65
	rm -fv $(DESTDIR)$(PREFIX)/lib/epple2/cards/stdin.ex65
	rm -fv $(DESTDIR)$(PREFIX)/lib/epple2/cards/clock.ex65
	rmdir -pv $(DESTDIR)$(PREFIX)/lib/epple2/cards/



clean:
	find . -name "*.ex65" | xargs rm -fv
	find . -name "*.o65" | xargs rm -fv
	rm -fv $(DIST).tar.gz
	rm -Rfv $(DIST)



mostlyclean: clean



distclean: clean
	rm -fv Makefile