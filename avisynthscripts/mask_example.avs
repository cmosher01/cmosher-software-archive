mask = ImageSource("e:/pvr/LittleEinsteinsMASK_ears_outline.png",end=1000000).ConvertToRGB32()
mask2 = ImageSource("e:/pvr/LittleEinsteinsMASK_ears.png",end=1000000).ConvertToRGB32()

darker = last.Tweak(bright=-50,cont=.9,sat=2)
lighter = last.Tweak(bright=28)

ConvertToRGB32()
Overlay(darker,mask=mask,mode="Darken")
Overlay(lighter,mask=mask2,mode="Lighten")
