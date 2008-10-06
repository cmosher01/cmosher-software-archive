SUFFIXES = .wxs .wixobj .msi

.wxs.wixobj:
	candle $< -out $@

.wixobj.msi:
	light $< -out $@

all: epple2.msi

epple2.wixobj: epple2.wxs
	candle $< -out $@ -dSDLDIR=/bin -dMINGWMDIR=/mingw/bin
