.SUFFIXES: .asciidoc .html

.asciidoc.html :
	asciidoc -b html5 $<

all : doc

doc : index.html

index.html : \
 index.asciidoc \
 screenshots.asciidoc \
 download.asciidoc \
 configuration.asciidoc \
 building.asciidoc \
 a2library.asciidoc \
 usermanual.asciidoc \
 overview.asciidoc \
 commands.asciidoc \
 display.asciidoc \
 keyboard.asciidoc \
 cards.asciidoc \
 cassette.asciidoc \
 paddles.asciidoc \
 speaker.asciidoc \


clean :
	rm -f index.html
