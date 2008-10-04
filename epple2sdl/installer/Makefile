.SUFFIXES: .wxs .wixobj .msi

.wxs.wixobj:
	candle $< -out $@

.wixobj.msi:
	light $< -out $@

all: epple2.msi
