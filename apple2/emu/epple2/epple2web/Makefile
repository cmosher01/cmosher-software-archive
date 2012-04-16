.SUFFIXES: .asciidoc .html

.asciidoc.html :
	asciidoc -b html5 $<

all : doc

doc : index.html

index.html : *.asciidoc *.png *.gif

clean :
	rm -f index.html
