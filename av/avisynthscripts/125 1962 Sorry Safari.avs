# from dvr-ms file, use DirectShowSource and Trim in script,
# and use VirtualDub with direct stream copy to create AVI file
#DirectShowSource("\\Fifi\Documents\Recorded TV\Tom & Jerry_TOON_14_07_2010_12_58_03.dvr-ms")
#Trim(59970,73150)

AviSource("125 1962 Sorry Safari.avi")
AssumeTFF()

Trim(8,13039)

DoubleWeave()
Pulldown(1,3)

AssumeFPS(24)
TimeStretch(100.1)

LanczosResize(704,528,taps=4)

LoadPlugin("C:/avisynth3rdpartyplugins/vaguedenoiser/vaguedenoiser-0.35.1/VagueDenoiser.dll")
VagueDenoiser(threshold=3,method=1,nsteps=6,chromaT=0,percent=90)

ConvertToYV12()

LoadPlugin("C:\avisynth3rdpartyplugins\deen\deen-beta-2\deen.dll")
Deen("c3d",0,10,12,3)

LoadPlugin("C:\avisynth3rdpartyplugins\masktools\masktools-2.0a36\mt_masktools-26.dll")
LoadPlugin("C:\avisynth3rdpartyplugins\removegrain\removegrain-1.0\removegrainsse3.dll")
Import("C:\avisynth3rdpartyplugins\limitedsharpen\LimitedSharpenFaster.avs")
LimitedSharpenFaster(strength=90,wide=true)

Tweak(cont=1.2)

DelayAudio(-0.5)
