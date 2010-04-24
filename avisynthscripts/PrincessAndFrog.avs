# DVD43 driver
# IsoPuzzle to copy to ISO file
# IfoEdit (angle 1) to create new VTS files
# DGIndex:
#   VTS_01_1.VOB - VTS_01_6.VOB
#   Allow DGIndex to fix field order transition
#   demux to wav; save project (VTS_01.d2v, *.wav)

LoadPlugin("C:\avisynth3rdpartyplugins\donald_graft\dgmpgdec\dgmpgdec-1.5.4\DGDecode.dll")
MPEG2Source("VTS_01.d2v",upConv=1,cpu=6)
Trim(1200,0)

#audio = WAVSource("VTS_01 T80 3_2ch 448Kbps 48KHz.wav")
#AudioDub(audio)


AssumeBFF()

Telecide(guide=1,post=2,vthresh=100.0,back=1,hints=true)
Decimate(mode=3,quality=3)

#TMpgEnc: (.tpr) (.avs+.wav -> .m2v+.wav)
#DVDlab: (.dal) (.m2v+.wav -> VIDEO_TS/+AUDIO_TS/)
#ImgBurn: (VIDEO_TS+AUDIO_TS/ -> .iso)
#ImgBurn: (.iso -> DVD)
