# DVD Decrypter: extract VTS_01_*.VOB
# DGIndex: demux to wav; save project (*.d2v, *.wav)
# (Allow DGIndex to fix field order transition)

LoadPlugin("C:\avisynth3rdpartyplugins\donald_graft\dgmpgdec\dgmpgdec-1.5.4\DGDecode.dll")
MPEG2Source("VTS_01.d2v",upConv=1,cpu=6)
audio = WAVSource("VTS_01 T80 2_0ch 192Kbps 48KHz.wav")
AudioDub(audio)
DelayAudio(-124)

AssumeBFF()

# "Madeline and the Pirates"
Trim(5626,44000)

Telecide(guide=1,post=2,vthresh=100.0,back=1,hints=true)
Decimate(mode=3,quality=3)

AssumeFPS(24)
TimeStretch(100.1)

LanczosResize(640,480,taps=4)


Crop(8,4,-8,-4)

LoadPlugin("C:/avisynth3rdpartyplugins/vaguedenoiser/vaguedenoiser-0.35.1/VagueDenoiser.dll")
VagueDenoiser(threshold=3,method=1,nsteps=6,chromaT=0,percent=90)

LoadPlugin("C:\avisynth3rdpartyplugins\masktools\masktools-2.0a36\mt_masktools-26.dll")
LoadPlugin("C:\avisynth3rdpartyplugins\removegrain\removegrain-1.0\removegrainsse3.dll")
Import("C:\avisynth3rdpartyplugins\limitedsharpen\LimitedSharpenFaster.avs")
LimitedSharpenFaster(strength=90,wide=true)

ConvertToYV12() # for x264

# MeGUI: x264: Unrestricted 2pass HQ; LAME MP3-128ABR
