LoadPlugin("C:\avisynth3rdpartyplugins\donald_graft\dgmpgdec\dgmpgdec-1.4.4.b2\DGDecode.dll")
MPEG2Source("C:\grinch\grinch_raw.d2v")
AssumeTFF()

orig = last

LoadPlugin("C:\avisynth3rdpartyplugins\vaguedenoiser\vaguedenoiser-0-34-1\VagueDenoiser.dll")
#VagueDenoiser(threshold=3,method=1,nsteps=6,chromaT=0,percent=90)
VagueDenoiser(threshold=4,method=1,nsteps=6,chromaT=0,wavelet=2,wiener=false,wratio=.5,percent=20)

Tweak(sat=1.2,cont=1.35,bright=-35)

LoadPlugin("C:\avisynth3rdpartyplugins\deen\deen-beta-1\deen.dll")
Deen("a2d",mode=3,thrY=6,thrUV=6,ti=0)

LoadPlugin("C:\avisynth3rdpartyplugins\warpsharp\WarpSharp.dll")
LoadPlugin("C:\avisynth3rdpartyplugins\warpsharp\aWarpSharp.dll")
LoadPlugin("C:\avisynth3rdpartyplugins\masktools\\MaskTools-v1.5.8\MaskTools.dll")
Import("C:\avisynth3rdpartyplugins\masktools\masktools-1.4.15.3\Sources\mfToon-v0.32.avs")
mfToon(strength=128)

LoadPlugin("C:\avisynth3rdpartyplugins\donald_graft\dup\dup-2.20.b1\Dup.dll")
Dup(threshold=4,blend=true)

#LoadPlugin("C:\avisynth3rdpartyplugins\super8equal\super8equal.dll")
#Super8Equal(center=1.0,rim=3.0,min=0.0,max=1.0)

StackHorizontal(orig,last)
