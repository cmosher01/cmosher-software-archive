LoadPlugin("C:\avisynth3rdpartyplugins\donald_graft\dgmpgdec\dgmpgdec-1.4.4.b2\DGDecode.dll")
video = MPEG2Source("C:\TRUE_HEART_BEAR\trueheartbear.d2v")

audio = WAVSource("C:\TRUE_HEART_BEAR\trueheartbear T01 2_0ch 192Kbps DELAY 0ms.wav")

AudioDub(video, audio)


#Trim(5030,24535) # Space Bubbles
Trim(24540,44053) # Cheer Bear's Chance

AssumeTFF()

#orig = last

LoadPlugin("C:\avisynth3rdpartyplugins\donald_graft\decomb\decomb-5.2.2\Decomb.dll")
Telecide(post=1,guide=1,vthresh=200,dthresh=2,gthresh=9,ovr="cheerbearschance.tel")

Decimate(mode=2,quality=3)




#orig = last

#LoadPlugin("C:\avisynth3rdpartyplugins\vaguedenoiser\vaguedenoiser-0-34-1\VagueDenoiser.dll")
#VagueDenoiser(threshold=3,method=1,nsteps=6,chromaT=0,percent=90)

#Tweak(sat=1.3,cont=1.4,bright=-30)

#LoadPlugin("C:\avisynth3rdpartyplugins\deen\deen-beta-1\deen.dll")
#Deen("a2d",mode=3,thrY=6,thrUV=6,ti=0)

#LoadPlugin("C:\avisynth3rdpartyplugins\warpsharp\WarpSharp.dll")
#LoadPlugin("C:\avisynth3rdpartyplugins\warpsharp\aWarpSharp.dll")
#LoadPlugin("C:\avisynth3rdpartyplugins\masktools\\MaskTools-v1.5.8\MaskTools.dll")
#Import("C:\avisynth3rdpartyplugins\masktools\masktools-1.4.15.3\Sources\mfToon-v0.32.avs")
#mfToon(strength=128)

#LoadPlugin("C:\avisynth3rdpartyplugins\donald_graft\dup\dup-2.20.b1\Dup.dll")
#Dup(threshold=4,blend=true)

#fixed = last

#StackHorizontal(orig,fixed)
