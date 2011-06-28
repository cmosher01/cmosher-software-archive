LoadPlugin("C:\avisynth3rdpartyplugins\donald_graft\dgmpgdec\dgmpgdec-1.5.4\DGDecode.dll")
MPEG2Source("Little Einsteins. { NR }(0).d2v",iCC=true,upConv=1,cpu=6)
LoadPlugin("C:\avisynth3rdpartyplugins\mpasource\mpasource.dll")
audio = MPASource("e:/pvr/Little Einsteins. { NR }(0) Tc0 L2 2ch 48 160 DELAY 0ms.mp2")
AudioDub(audio)

AssumeTFF()

Trim(569,43603)

LoadPlugin("C:\avisynth3rdpartyplugins\donald_graft\decomb\decomb-5.2.3\Decomb.dll")
Telecide(guide=0,post=0,show=false,debug=false,hints=false,ovr="Little Einsteins. { NR }(0)_2.ovr")
Decimate(mode=2,cycle=5)
AssumeFPS(24)
TimeStretch(100.1)

LanczosResize(640,480,taps=4)

Crop(8,4,-16,-4)
AddBorders(12,4,12,4)

mask = ImageSource("e:/pvr/Little Einsteins. { NR }(0).mask.png",0,0).ConvertToYUY2()

nologo_1 = Trim(0,2484)
logo = Trim(2485,42172)
nologo_2 = Trim(42173,43034)
LoadPlugin("c:/avisynth3rdpartyplugins/warpsharppkg/loadpluginex.dll")
LoadPlugin("C:/avisynth3rdpartyplugins/logotools/logotools.dll")
audio_logo = logo.GetChannel(1,2)
logo = logo.NoLogoM(mask,2380,10,28,340,160,136,1.0,240,0)
logo = logo.AudioDub(audio_logo)

last = nologo_1+logo+nologo_2


LoadPlugin("C:/avisynth3rdpartyplugins/vaguedenoiser/vaguedenoiser-0.35.1/VagueDenoiser.dll")
VagueDenoiser(threshold=3,method=1,nsteps=6,chromaT=0,percent=90)

Tweak(sat=1.2)

LoadPlugin("C:\avisynth3rdpartyplugins\masktools\masktools-2.0a36\mt_masktools-26.dll")
LoadPlugin("C:\avisynth3rdpartyplugins\removegrain\removegrain-1.0\removegrainsse3.dll")
Import("C:\avisynth3rdpartyplugins\limitedsharpen\LimitedSharpenFaster.avs")
LimitedSharpenFaster(strength=90,wide=true)


ConvertToYV12() # for x264

Crop(12,4,-12,-4)

return last
